/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENT_PACKEG;

/**
 *
 * @author uzair
 */
// Java implementation for a client 
// Save file as Client.java 
import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class 
public class Client {
    static Credentials credentials;
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);  // Reading from System.in

        FileHandler fileHandler = new FileHandler();
         credentials = null;
        do {
            System.out.println("Please Enter configration file name");
            String filename = reader.nextLine();
            credentials = fileHandler.ReadCredencials(filename);//"E:\\config.txt");

        } while (credentials == null);
        System.out.println(credentials.toString());
        
        startClient(Integer.parseInt(credentials.getPort()));
    }

    private static void startClient(int port) {
        System.out.println("Trying to connect to server...");
            
        try {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip 
//            InetAddress ip = InetAddress.getByName("localhost");
            InetAddress ip = InetAddress.getByName(credentials.getServerName());

            // server is listening on port 5056 
            Socket s = new Socket(ip, port);
            System.out.println("Connected!");
            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of 
            // information between client and client handler 
            String line;
            String[] list;
            
            while (true) {
                line=dis.readUTF();
                list=line.split("~");
                if(list.length>1)
                {
                    if(list[0].equals("1"))//Display only
                        System.out.println(list[1]);
                    continue;    
                }
                System.out.println(line);
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                // If client sends exit,close this connection 
                // and then break from the while loop 
                if (tosend.equals("Exit")) {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
            }

            // closing resources 
            scn.close();
            dis.close();
            dos.close();
        } catch (Exception e) {
            System.out.println("Unable to connect to server localhost on port "+port);
            //e.printStackTrace();
        }
    }

}
