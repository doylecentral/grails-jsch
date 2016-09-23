import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import jschssh.RunSshCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.GenericGroovyApplicationContext
import spock.lang.Specification

/**
 * Created by doyle on 9/6/16.
 */
@TestMixin(GrailsUnitTestMixin)
class RunSshCommandSpec extends Specification {
//    static loadExternalBeans =true//this loads beans present in resources.groovy
    @Autowired
    RunSshCommand runSshCommand


    def 'Run Hello Echo'() {

        def context = new GenericGroovyApplicationContext()
        context.reader.beans {
            connectionInfo(jschssh.ConnectionInfo) {
                username = "root"
                //        password = null
                //        keyFile = null
                //        keyFilePassword = null
                //        port = 22
                //        strictHostKeyChecking = "no"
                //        knownHostsFile = "~/.ssh/known_hosts"
                //        sshConfigFile = "~/.ssh/config"
                //        connectionTimeout = 0
                //        preserveTimeStamps = false
                //        // Normal File Read + Write for user,
                //        // Read for group and Everyone
                //        defaultFilePermission = "0644"
            }
            runSshCommand(RunSshCommand , connectionInfo)
        }
        context.refresh()


    RunSshCommand command = context.getBean('runSshCommand')

    when:
    println "Hello"
    then:
    assert command != null
    true == true
}

    def 'Run Hello Echo Real'(){
        RunSshCommand runSshCommand = new RunSshCommand()
        runSshCommand.host = "172.16.180.143"
        runSshCommand.username = "doyle"
        runSshCommand.password = "doyle"

        runSshCommand.keyFile = null
        runSshCommand.keyFilePassword = null
        runSshCommand.port = 22
        runSshCommand.strictHostKeyChecking = "no"
        //Setting to known hosts that does not exists
        runSshCommand.knownHostsFile = "~/.ssh/known_hosts_js"
        runSshCommand.sshConfigFile = "~/.ssh/config"
        runSshCommand.connectionTimeout = 0
        //runSshCommand.preserveTimeStamps = false
        // Normal File Read + Write for user,
        // Read for group and Everyone
        //runSshCommand.defaultFilePermission = "0644"

        when:
            runSshCommand.command = "echo HELLO"
        then:
            runSshCommand.execute() == "HELLO\n"
    }

}
