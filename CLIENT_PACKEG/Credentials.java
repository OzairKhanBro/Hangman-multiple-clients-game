/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENT_PACKEG;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uzair
 */
public class Credentials {
    private String serverName,port,username,password,wordFileName;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWordFileName() {
        return wordFileName;
    }

    public void setWordFileName(String wordFileName) {
        this.wordFileName = wordFileName;
    }

    @Override
    public String toString() {
        return "Server Hostname – "+serverName+
                    "\nServer Port – "+port+"\n"; //To change body of generated methods, choose Tools | Templates.
    }
    
}
