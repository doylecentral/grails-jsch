package com.dc

import grails.plugins.*
import jschssh.RunSshCommand

class JschGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.9 > *"
    // resources that are excluded from plugin packaging

    // TODO Fill in these fields
    def title = "Jsch Ssh2 Grails Plugin" // Headline display name of the plugin
    def author = "Brian Doyle"
    def authorEmail = "doylecentral@gmail.com"
    def description = '''\
Brief summary/description of the plugin.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/jsch-ssh2-grails-plugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() {
        { ->
            runSshCommand(RunSshCommand, ref('connectionInfo'))

            connectionInfo(jschssh.ConnectionInfo) {
                username = grailsApplication.config.username
                password = grailsApplication.config.password
                keyFile = grailsApplication.config.keyFile
                keyFilePassword = grailsApplication.config.keyFilePassword
                port = grailsApplication.config.sshport
                strictHostKeyChecking = grailsApplication.config.strictHostKeyChecking
                knownHostsFile = grailsApplication.config.knownHostsFile
                sshConfigFile = grailsApplication.config.sshConfigFile
                connectionTimeout = grailsApplication.config.connectionTimeout
                // Normal File Read + Write for user,
                // Read for group and Everyone
                defaultFilePermission = grailsApplication.config.defaultFilePermission
            }

        }
    }
}
