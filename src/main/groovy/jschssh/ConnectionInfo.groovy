package jschssh

import com.jcraft.jsch.ConfigRepository
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.apache.log4j.Logger
import org.springframework.beans.factory.InitializingBean

/**
 * Created with IntelliJ IDEA.
 * User: Michael Rice
 * Twitter: @errr_
 * Website: http://www.errr-online.com/
 * Github: https://github.com/michaelrice
 * Date: 5/17/2014
 * Time: 6:50 PM
 * Licenses: MIT http://opensource.org/licenses/MIT
 */

public class ConnectionInfo implements InitializingBean{

    /**
     * Logger
     */
    private Logger log = Logger.getLogger(ConnectionInfo)

    String username

    /**
     * Password from the config file.
     *
     */
    String password //= config.password

    /**
     * Full path to the key file.
     * Tomcat user must have read access to this file.
     *
     */
    String keyFile //= config.keyFile

    /**
     * Password for the key file.
     *
     */
    String keyFilePassword //= config.keyFilePassword

    /**
     * This setting allows you to ignore hosts you have not accepted a key
     * for. This is handy in development mode, but should not be used in
     * in production. The default is yes. In development mode set to "no".
     *
     */
    String strictHostKeyChecking //= config.strictHostKeyChecking

    /**
     * The port the remote ssh server is listening on. Default is 22.
     */
    int port// = config.port

    /**
     * Full path to the ssh known hosts file. The tomcat user will need
     * read access to this file. The default is ~/.ssh/known_hosts
     *
     */
    String knownHostsFile //= config.knownHostsFile

    /**
     * Hostname of the remote host you wish to connect to.
     * This can be a hostname or an IP address.
     *
     */
    String host

    /**
     * The full path to the ssh config file you wish to load.
     * The file is loaded first, so options in the class
     * and in the application config will over ride what is in
     * the file.
     *
     * For example you set sshCondifgFile = "~/.ssh/config"
     * Next in this file you have the following option set:
     *
     * StrictHostKeyChecking yes
     *
     * You can over ride that in the class by setting:
     * this.strictHostKeyChecking = "no"
     *
     * Or by using the Config.groovy and setting:
     * jschSsh2 {
     *     StrictHostKeyChecking = "no"
     * }
     *
     * This is to allow you to over ride settings while in
     * development mode.
     */
    String sshConfigFile //= config.sshConfigFile

    /**
     * Connection timeout for connecting to a remote host.
     */
    int connectionTimeout //= config.connectionTimeout

    /**
     * defaultFilePermission
     */
    String defaultFilePermission

    /**
     * Runs a passed closure to implement builder-style operation.
     *
     * @param closure
     */
    public void run(Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.OWNER_FIRST
        closure.call()
    }

    int checkAck(InputStream inputStream ) throws IOException {
        int b = inputStream.read()
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b
        }

        if (b == -1) {
            return b
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer()
            inputStream.eachByte {
                if (it != "\n") {
                    sb.append(it)
                }
            }

            if (b == 1) { // error
                log.error(sb.toString())
            }
            if (b == 2) { // fatal error
                log.error(sb.toString())
            }
        }
        return b
    }

    @Override
    void afterPropertiesSet() throws Exception {

        assert this.port != null , "SSH Port must not be null"

    }
}