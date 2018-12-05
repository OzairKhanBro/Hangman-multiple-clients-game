/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multyclientgamme;

/**
 *
 * @author uzair
 */
// Java implementation of Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
import com.mysql.cj.xdevapi.Statement;
import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import java.io.*;
import static java.lang.Integer.parseInt;
import java.text.*;
import java.util.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

// Server class 
public class Server {

    static DBmanager dbmanager;
    static final ConcurrentHashMap<String, CountDownLatch> gameNameList
            = new ConcurrentHashMap<String, CountDownLatch>();
    static final ConcurrentHashMap<String, GroupGameManager> groupGames
            = new ConcurrentHashMap<String, GroupGameManager>();
    public static String[] WordList;
    public final DateFormat date = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void Start() throws FileNotFoundException {
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        
        FileHandler fileHandler = new FileHandler();
        Credentials credentials = null;
        do {
            System.out.println("Please Enter configration file name");
            String filename = reader.nextLine();
            credentials = fileHandler.ReadCredencials(filename);//"E:\\config.txt");

        } while (credentials == null);
        WordList=fileHandler.ReadCompleteFile(credentials.getWordFileName()).split("\r|\n");
        System.out.println(credentials);
        dbmanager = new DBmanager(credentials);
        dbmanager.testConnection();
        StartServer(Integer.parseInt(credentials.getPort()));
    }

    private static void StartServer(int port) {
        System.out.println("Trying to start Server...");
        // server is listening on port 5056 
        ServerSocket ss;
        try {
            ss = new ServerSocket(port);
            System.out.println("Connected!\nServer is running...");
        } catch (IOException ex) {
            System.out.println("Unable to connect to server on port " + port);
            //printStackTrace();
            return;
        }
        // running infinite loop for getting 
        // client request 
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests 
                s = ss.accept();
                DataInputStream dis=new DataInputStream(s.getInputStream());  
                DataOutputStream dos =new DataOutputStream(s.getOutputStream());
                ClientHandler a = new ClientHandler(s,dis, dos);
                a.start();
            } catch (Exception e) {
                try {
                    s.close();
                } catch (IOException ex) {
                    //ex.printStackTrace();//Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Having trouble to accept connection request.");
                //e.printStackTrace();
            }
        }
    }

    

}
