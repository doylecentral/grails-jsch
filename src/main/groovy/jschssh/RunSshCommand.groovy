package jschssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.ConfigRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.Resource


/**
 * Created with IntelliJ IDEA.
 * User: Michael Rice
 * Twitter: @errr_
 * Website: http://www.errr-online.com/
 * Github: https://github.com/michaelrice
 * Date: 5/11/2014
 * Time: 4:00 PM
 * Licenses: MIT http://opensource.org/licenses/MIT
 */

class RunSshCommand{ // extends ConnectionInfo {

    /**
     * Logger
     */
    private Logger log = Logger.getLogger(RunSshCommand)

    /**
     * The command you wish to run on the remote server.
     *
     */
    public String command


    //@Resource(name="connectionInfo")
    @Autowired
    public ConnectionInfo connectionInfo

    /**
     * This provides a builder style method to execute a command on
     * a remote server using the ssh protocol.
     *
     * @param closure
     * @return String with the output of the command that ran.
     */
    public String execute(Closure closure) {
        run closure
        execute()
    }

    public  RunSshCommand(){}


    public RunSshCommand(ConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo
    }

    /**
     * This provides a method to execute a command on a remote host
     * using the ssh protocol.
     *
     * @param command
     * @return
     */
    public String execute() throws JSchException {
        try {
            Session session = fetchSession()
            log.trace("running command.")
            // Open channel to run command.
            ChannelExec channel = session.openChannel("exec")
            channel.setCommand(command)
            log.trace("set command on channel")
            channel.setInputStream(null)
            // TODO is there a better way to do this in groovy?
            // Right now its just copied from the jsch java examples.
            StringBuilder response = new StringBuilder()
            InputStream inputStream = channel.inputStream
            InputStream errorStream = channel.errStream
            channel.connect()
            int x = 0
            // Wait for the channel to close aka command to finish running.
            while (!channel.closed) {
                sleep(10)
                log.trace("Waiting 10 miliseconds for command to finish executing. ${x+1}")
                x++
                // "esxcli --formatter=xml hardware pci list"
                // got stuck and would never show closed
                if (x >= 30) {
                    // even after this it still showed -1 for exit status
                    break
                }
            }
            // If we get a non 0 exit status we need to read from the
            // error stream to return the user what the server said.
            if (channel.exitStatus > 0) {
                log.trace("Getting error stream. ${channel.exitStatus}")
                response = parseStream(errorStream, response, channel)
            }
            else {
                log.trace("Getting input stream. ${channel.exitStatus}")
                response = parseStream(inputStream, response, channel)
            }

            channel.disconnect()
            session.disconnect()
            log.debug("Successfully ran command on remote server. ${command}")
            return response.toString()
        }
        catch (JSchException e) {
            log.error("Error trying to execute command.",e)
            throw e
        }
        catch (Exception e1) {
            log.fatal("An unexpected Exception has happened.", e1)
            throw e1
        }
        return null
    }

    private StringBuilder parseStream(InputStream inputStream, StringBuilder response, ChannelExec channel) {
        byte[] tmp = new byte[1024]
        log.trace("Parsing Command stream to generate user output.")
        while (true) {
            // There is something in this stream to read
            while (inputStream.available() > 0) {
                int i = inputStream.read(tmp, 0, 1024)
                if (i < 0) {
                    break
                }
                // Append to our response.
                response.append(new String(tmp,0,i))
            }
            if (channel.isClosed()) {
                if (inputStream.available() > 0) {
                    continue
                }
                log.trace("exit-status: " + channel.getExitStatus())
                break
            }
        }
        return response
    }

    public Session fetchSession() throws JSchException {
        try {
            log.debug("Opening connection on remote server.")
            JSch jSch = new JSch()
            // session object used once connected
            Session session

            // if the hosts file variable has been set then attempt
            // to load into JSch object.
            if (connectionInfo.knownHostsFile) {
                log.trace("Adding known hosts file to client.")
                jSch.setKnownHosts(connectionInfo.knownHostsFile)
            }

            // If the config file is set attempt to load it
            if (connectionInfo.sshConfigFile) {
                log.trace("Loading ssh config file")
                ConfigRepository configRepository = com.jcraft.jsch.OpenSSHConfig.parse(connectionInfo.sshConfigFile)
                jSch.setConfigRepository(configRepository)
            }

            // If keyFile is set and password is not attempt to use the key to auth
            if (connectionInfo.keyFile && !connectionInfo.password) {
                log.trace("Attempting an ssh key auth.")
                if (connectionInfo.keyFilePassword) {
                    log.trace("Adding ${keyFile}, and keyFilePassword to identity.")
                    jSch.addIdentity(connectionInfo.keyFile, connectionInfo.keyFilePassword)
                }
                else {
                    log.trace("Adding ${connectionInfo.keyFile} to identity.")
                    jSch.addIdentity(connectionInfo.keyFile)
                }
            }
            log.trace("Opening session to remote host.")
            session = jSch.getSession(connectionInfo.username, connectionInfo.host, connectionInfo.port)
            // If the connectionTimeout is set use it instead of jsch default.
            if (connectionInfo.connectionTimeout) {
                session.timeout = connectionInfo.connectionTimeout
            }

            if (connectionInfo.password) {
                // If this is not set maybe its a key auth?
                session.setPassword(connectionInfo.password)
            }

//            session.setConfig("StrictHostKeyChecking","no")
            session.setConfig("StrictHostKeyChecking", connectionInfo.strictHostKeyChecking)

            // Connect to the server to run the command.
            session.connect()
            log.trace("connected to server.")
            return session
        }
        catch (JSchException e) {
            log.debug("Failed to create session to host.")
            throw e
        }
    }
}
