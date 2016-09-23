package jschssh
/**
 * Created by doyle on 9/7/16.
 */
class BaseJschConfig {
    /**
     * Refactor to make this in some type of config
     */

    String username = null
    String password = null
    String keyFile = null
    String keyFilePassword = null
    String port = 22
    String strictHostKeyChecking = "yes"
    String knownHostsFile = "~/.ssh/known_hosts"
    String sshConfigFile = "~/.ssh/config"
    String connectionTimeout = 0
    String preserveTimeStamps = false
    // Normal File Read + Write for user,
    // Read for group and Everyone
    String defaultFilePermission = "0644"

}
