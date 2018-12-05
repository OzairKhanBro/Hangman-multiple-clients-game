/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multyclientgamme;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import static multyclientgamme.Server.gameNameList;
import static multyclientgamme.Server.dbmanager;
import static multyclientgamme.Server.groupGames;

/**
 *
 * @author uzair
 */
public class ClientHandler extends Thread {

    DataInputStream dis;
    DataOutputStream dos;
    Socket s;
    Person person;
    GroupGameManager group = null;
    String currentGameName;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
    }

    void sentMessage(String msg) throws IOException {
        dos.writeUTF(msg);
    }

    String reciveMessage() throws IOException {
        return dis.readUTF();
    }

    @Override
    public void run() {

        try {
            ManageClient();
            if (group != null) {
                group.join();
            } else {
                Server.gameNameList.remove(this.currentGameName);            
            }
            dos.writeUTF("1~Thank you for playing Hangman!");
            dis.close();
            dos.close();
            s.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            //Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            System.out.println(CurrentTime() + person.getName()+" - successfully log out.");
        
    }

    public void ManageClient() throws IOException, InterruptedException {
        //System.out.println(CurrentTime() + "A new client is connected : ");

        // obtaining input and out streams 
        Person p = Login(dis, dos);
        person = p;
        System.out.println(CurrentTime() + p.getName() + "- has record " + p.getWin() + " wins and " + p.getLoss() + " losses.");
        System.out.println(CurrentTime() + p.getName() + "- successfully logged in.");

        //get options
        dos.writeUTF("Great! You are now logged in as " + p.getName() + "!\n"
                + p.getName() + "’s Record\n"
                + "--------------\n"
                + "Wins – " + p.getWin() + "\n"
                + "Losses – " + p.getLoss() + "\n\nPress\n1) Start a Game\n2) Join a Game\n"
                + "Would you like to start a game or join a game?");

        String pressed;
        do {
            //     dos.writeUTF("Press 1 to create new account\n Press 2 to renter username and Password\n");
            pressed = dis.readUTF();
            if (pressed.equals("1") || pressed.equals("2")) {
                break;
            }
            dos.writeUTF("Please Enter Valid number \nPress\n 1) Start a Game\n2) "
                    + "Join a Game\nWould you like to start a game or join a game?");
        } while (true);

        String gameName;

        dos.writeUTF("What is the name of the game?");
        do {
            gameName = dis.readUTF();
            if (pressed.equals("1")) {
                if (!gameNameList.contains(gameName)) {
                    break;
                } else {
                    dos.writeUTF(gameName + " already exists \nplease renter name of the game.");
                }
            } else if (pressed.equals("2")) {
                if (!gameNameList.containsKey(gameName)) {
                    dos.writeUTF("There is no game with name " + gameName
                            + "\nplease renter name of the game.");
                } else if (gameNameList.containsKey(gameName)
                        && gameNameList.get(gameName).getCount() == 0) {
                    dos.writeUTF(gameName + " exists, but"+p.getName()+
                            "unable to join because maximum number of players have already joined"+gameName);
                System.out.println(CurrentTime() + p.getName() + " - wants to join a game called " + gameName);
            
                } else if (gameNameList.containsKey(gameName)
                        && gameNameList.get(gameName).getCount() != 0) {
                    break;
                }
            }

        } while (true);
        currentGameName = gameName;
        if (pressed.equals("1")) {
            System.out.println(CurrentTime() + p.getName() + " - wants to start a game called " + gameName);
            dos.writeUTF("How many users will be playing (1-4)?");
            int user;
            do {
                try {
                    user = Integer.parseInt(dis.readUTF());
                    if (user > 0 && user < 4) {
                        break;
                    }
                    throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    dos.writeUTF("A game can only have between 1-4 players.\n"
                            + "Please Enter a valid number.\nHow many users will be playing (1-4)?");
                }

            } while (true);

            System.out.println(CurrentTime() + p.getName() + " - successfully started game " + gameName);
            if (user > 1) {
                GroupGameManager g = new GroupGameManager(user);
                gameNameList.put(gameName, g.getLeach());
                group = g;
                g.addPartner(this);
                g.setGameName(gameName);
                groupGames.put(gameName, group);
                System.out.println(CurrentTime() + p.getName() +" "+
                    gameName+" needs " + user+" players to start game.");
            
                g.start();
            } else {
                GroupGameManager g = new GroupGameManager(user);
                gameNameList.put(gameName, g.getLeach());
                group = g;
                g.addPartner(this);
                g.setGameName(gameName);
                groupGames.put(gameName, group);
                System.out.println(CurrentTime() + p.getName() +" "+
                    gameName+" successfully started game ");
            
                g.start();
            }

        } else if (pressed.equals("2")) {
            System.out.println(CurrentTime() + p.getName() + " - wants to join a game called " + gameName);
            if (gameNameList.containsKey(gameName)
                    && gameNameList.get(gameName).getCount() != 0) {
                group = groupGames.get(gameName);
                gameNameList.put(gameName, groupGames.get(gameName).getLeach());
                groupGames.get(gameName).addPartner(this);
            }
        }

        // create a new thread object 
        //Thread t = new SingleClientHandler(dis, dos);
        // Invoking the start() method 
        //t.start();
    }

    public static String CurrentTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        String formattedDate = dateFormat.format(date);
        return " " + formattedDate + " ";
    }

    private static Person Login(DataInputStream dis, DataOutputStream dos) throws IOException {
        String message = null;
        while (true) {
            dos.writeUTF("\nPlease Enter your user name : ");
            message = null;
            String name = dis.readUTF();
            dos.writeUTF("Please Enter your Password : ");
            String password = dis.readUTF();

            System.out.println(CurrentTime() + name + " - trying to log in with password " + password);
            Person p = Server.dbmanager.findPersonwithPassword(name, password);
            if (p == null) {
                dos.writeUTF("1~No account exists with those credentials.");
                System.out.println(CurrentTime() + name + " - does not have an account so not successfully logged in.");
                String pressed;
                do {
                    dos.writeUTF("Press 1 to create new account\nPress 2 to reenter username and Password.");
                    pressed = dis.readUTF();
                    if (pressed.equals("1") || pressed.equals("2")) {
                        break;
                    } else {
                        dos.writeUTF("1~Please Enter Valid number");
                    }
                } while (true);
                if (pressed.equals("1")) {
                    dos.writeUTF("Please Enter your user name : ");
                    name = dis.readUTF();
                    dos.writeUTF("Please Enter your Password : ");
                    password = dis.readUTF();
                    p = new Person();
                    p.setName(name);
                    p.setPassword(password);
                    Server.dbmanager.InsertPerson(p);
                    System.out.println(CurrentTime() + name + "- created an account with password " + password);
                    return p;
                }
            } else if (p.name == null) {
                System.out.println(CurrentTime() + name + "- has an account but not successfully logged in.");
                dos.writeUTF("1~You enterd wrong Password.");
                //message = "You enterd wrong Password.";
            } else {

                return p;
            }
        }

    }

}
