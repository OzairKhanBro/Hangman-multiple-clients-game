/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multyclientgamme;

import java.io.FileNotFoundException;
import java.sql.Time;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author uzair
 */
public class MultyClientGamme {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        
        Server.Start();
        
        // TODO code application logic here
//        FileHandler fileHandler = new FileHandler();
//            
//        Credentials  credentials = fileHandler.ReadCredencials("E:\\config.txt");
//        System.out.println(credentials);
//        DBmanager d = new DBmanager(credentials);
//        Person p=d.findPerson("username", "asdf");
//        System.out.println(p.id);

//        Scanner reader = new Scanner(System.in);  // Reading from System.in
//        FileHandler fileHandler = new FileHandler();
//        String s=fileHandler.ReadCompleteFile("E:\\hangmanwords.txt");
        //System.out.println(s);
//        String[] sa=s.split("\r|\n");
//        for (int i = 0; i < sa.length; i++) {
//            String string = sa[i];
//            System.out.print(string);
//            if(string.equals("acres"))
//                System.out.println("#####################");
//        }
//        
        Random r=new Random();
        System.out.println(r.nextInt(50));
//        FileHandler fileHandler = new FileHandler();
//        Credentials credentials = null;
//        do {
//            System.out.println("Please Enter configration file name");
//            //String filename = reader.nextLine();
//            credentials = fileHandler.ReadCredencials("E:\\config.txt");
//
//        } while (credentials == null);
//        System.out.println(credentials);
//        DBmanager d = new DBmanager(credentials);
//        d.testConnection();
//        System.out.println(d.findPersonwithPassword("asdf", "123").id);
//          Hangman h=new Hangman("uzaira");
//          h.start();
//        String s="1asd";
//
//        System.out.println(s.split("~").length);
//        
//        SingleClientHandler s = new SingleClientHandler();
//        s.start();
//        synchronized(s)
//        {
//            System.out.println("in wating state");
//            s.wait();
//        s.outpuawe();
//            
//        }
//        s.join();
//        s.join();
//        System.out.println("i am here Parallel");
    }

}
