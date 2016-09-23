import grails.test.mixin.TestMixin
import grails.test.mixin.integration.Integration
import grails.test.mixin.support.GrailsUnitTestMixin
import jschssh.ConnectionInfo
import jschssh.RunSshCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericGroovyApplicationContext
import spock.lang.Specification

/**
 * Created by doyle on 9/6/16.
 */
@Integration
class RunSshCommandSpec extends Specification {

    @Autowired
    RunSshCommand runSshCommand

    def 'With Spring Config defaults check'(){
        ConnectionInfo connectionInfo
        when:
            connectionInfo = runSshCommand.connectionInfo
        then:
            connectionInfo.port == 22
            connectionInfo.username == "root"
            connectionInfo.password == null
            connectionInfo.keyFile == null
            connectionInfo.keyFilePassword == null
            connectionInfo.strictHostKeyChecking == "no"
            connectionInfo.knownHostsFile == "~/.ssh/known_hosts"
            connectionInfo.sshConfigFile == "~/.ssh/config"
            connectionInfo.connectionTimeout == 0
            connectionInfo.defaultFilePermission == "0644"
    }

    def 'With Spring Config Run'(){

        when:
            runSshCommand.connectionInfo.host = "172.16.180.143"
            runSshCommand.connectionInfo.username = "doyle"
            runSshCommand.connectionInfo.password = "doyle"

            runSshCommand.command = "echo HELLO"
        then:
            runSshCommand.execute() == "HELLO\n"

    }


    def 'Run Hello Echo Connection local'(){

        ConnectionInfo connectionInfo = new ConnectionInfo()
        connectionInfo.host = "172.16.180.143"
        connectionInfo.username = "doyle"
        connectionInfo.password = "doyle"

        connectionInfo.keyFile = null
        connectionInfo.keyFilePassword = null
        connectionInfo.port = 22
        connectionInfo.strictHostKeyChecking = "no"

        connectionInfo.knownHostsFile = "~/.ssh/known_hosts_js"
        connectionInfo.sshConfigFile = "~/.ssh/config"
        connectionInfo.connectionTimeout = 0

        RunSshCommand runSshCommand = new RunSshCommand(connectionInfo)
        when:
            runSshCommand.command = "echo HELLO"
        then:
            runSshCommand.execute() == "HELLO\n"
    }

}
