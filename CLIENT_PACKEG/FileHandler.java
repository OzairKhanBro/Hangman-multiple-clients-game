/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENT_PACKEG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author uzair
 */
public class FileHandler {

   public Credentials ReadCredencials(String fileName) {
        String line;
        Credentials c = null;
        try {
            File file = new File(fileName);
            Scanner sc = new Scanner(file);
            c = new Credentials();
            while (sc.hasNextLine()) {
                //System.out.println(sc.nextLine());
                line = sc.nextLine();
                String[] key = line.split("=");
                if (key[0].equals("ServerHostname")) {
                    c.setServerName(key[1]);
                } else if (key[0].equals("ServerPort")) {
                    c.setPort(key[1]);
                }
            }
            boolean ca=true;
            if (c.getPort() == null) {
                ca = false;
                System.err.println("ServerPort is a required parameter in the configuration file.");
            }
            if (c.getServerName() == null) {
                ca = false;
                System.err.println("ServerHostname is a required parameter in the configuration file.");
            }
            if(ca==false)c=null;
        } catch (FileNotFoundException ex) {
            System.out.println("Configuration file " + fileName + " could not be found");
            //ex.printStackTrace();
            //Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }

    public String ReadCompleteFile(String file) {
        String fileContent = null;
        File crunchifyFile = new File(file);
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(crunchifyFile);
            byte[] crunchifyValue = new byte[(int) crunchifyFile.length()];
            fileInputStream.read(crunchifyValue);
            fileInputStream.close();

            fileContent = new String(crunchifyValue, "UTF-8");
            //   log(fileContent);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Unable to locate file");
                    
            //e.printStackTrace();
        }
        return fileContent;
    }
    // Always close files.

    

}
