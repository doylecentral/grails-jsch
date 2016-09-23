import jschssh.RunSshCommand

beans {

    connectionInfo(jschssh.ConnectionInfo){
        username = "root"
        password = null
        keyFile = null
        keyFilePassword = null
        port = 22
        strictHostKeyChecking = "no"
        knownHostsFile = "~/.ssh/known_hosts"
        sshConfigFile = "~/.ssh/config"
        connectionTimeout = 0
        preserveTimeStamps = false
        // Normal File Read + Write for user,
        // Read for group and Everyone
        defaultFilePermission = "0644"
    }

    runSshCommand(RunSshCommand, ref('connectionInfo'))

//    runSshCommand(RunSshCommand){ beanDefinitions ->
//        beanDefinitions.constructorArgs = [ref('connectionInfo')]
//    }

}