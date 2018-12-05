/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multyclientgamme;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import static multyclientgamme.ClientHandler.CurrentTime;
import org.omg.PortableServer.Servant;

/**
 *
 * @author uzair
 */
public class GroupGameManager extends Thread {

    CopyOnWriteArrayList<ClientHandler> list;
    CountDownLatch leach;
    String gameName;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    int maxNumberOfUsers;

    GroupGameManager(int user) {
        maxNumberOfUsers = user;
        leach = new CountDownLatch(user);
        list = new CopyOnWriteArrayList<ClientHandler>();

    }

    public void addPartner(ClientHandler c) throws IOException {
        if (c != null && list.size() < maxNumberOfUsers) {
            for (int i=0 ; i<list.size();i++) {
                Person p = list.get(i).person;
                if(list.get(i).person.getName().equals(c.person.getName()))
                    continue;
                String line = "1~" + " User " + p.getName() + " is in the game.\n"
                        + p.getName() + "’s Record\n"
                        + "--------------\n"
                        + "Wins – " + p.getWin() + "\n"
                        + "Losses – " + p.getLoss();
                list.get(i).sentMessage(line);
            }
            for (ClientHandler clint : list) {
                Person p = clint.person;
                String line = "1~" + " User " + p.getName() + " is in the game.\n"
                        + p.getName() + "’s Record\n"
                        + "--------------\n"
                        + "Wins – " + p.getWin() + "\n"
                        + "Losses – " + p.getLoss();
                c.sentMessage(line);
            }
            System.out.println(CurrentTime() + c.person.getName() + " successfully joined game " + gameName);

            list.add(c);
            leach.countDown();
        }
    }

    public void sentToAllUsers(String msg) throws IOException {
        for (ClientHandler clint : list) {
            clint.sentMessage(msg);
        }
    }

    public void sentToAllUsersExcept(String msg, int c) throws IOException {
        for (int i = 0; i < list.size(); i++) {
            ClientHandler clint = list.get(i);
            if (i != c) {
                clint.sentMessage(msg);
            }
        }
    }

    public void sentToSpecipifUser(String msg, int c) throws IOException {
        ClientHandler clint = list.get(c);
        clint.sentMessage(msg);
    }

    public String getInput(int c) throws IOException {
        ClientHandler clint = list.get(c);
        return clint.reciveMessage();
    }

    @Override
    public void run() {

        try {
            list.get(0).sentMessage("1~Waiting for " + leach.getCount() + " other user to join...");
            System.out.println(ClientHandler.CurrentTime() + " "
                    + " needs " + leach.getCount() + " Players to start game.");
            leach.await();
            sentToAllUsers("1~All users have joined.\nDetermining secret word...");
            System.out.println(CurrentTime() +" successfully started game " + gameName);
            System.out.println(CurrentTime() + gameName + " has " + maxNumberOfUsers + " players so starting game.");
            String word=null;
            do{
                synchronized(Server.WordList){
                    int r = new Random().nextInt(Server.WordList.length);
                    word = Server.WordList[r];
                }
            }while(word==null || word.equals(""));
            System.out.println(CurrentTime() +gameName+" Secret word is " + word);

            StartGame(word);
        } catch (Exception ex) {
            System.out.println(CurrentTime()+" -Error occured in group game "+gameName);
            //ex.printStackTrace();
//    Logger.getLogger(GroupGameManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        Server.groupGames.remove(this.gameName);
    }

    CountDownLatch getLeach() {
        return leach;
    }

    public void StartGame(String secratword) throws IOException {
//initialize
        String word = secratword;
        String asterisk;
        int count = 7;
        int turn = 0;
        word = word.toUpperCase();
        asterisk = new String(new char[word.length()]).replace("\0", "_");
        String guess = null;
        while (count != 0 && !asterisk.equals(word)) {

            sentToAllUsersExcept("1~Waiting for "
                    + list.get(turn).person.getName() + " to do something...", turn);
            sentToAllUsers("1~Secret Word: " + asterisk);
            String pressed;
            do {
                sentToSpecipifUser(
                        "You have " + count + " incorrect guesses remaining.\n"
                        + "1) Guess a letter.\n"
                        + "2) Guess the word.\n"
                        + "What would you like to do?", turn);
                //     dos.writeUTF("Press 1 to create new account\n Press 2 to renter username and Password\n");
                pressed = getInput(turn);
                if (pressed.equals("1") || pressed.equals("2")) {
                    break;
                }
                sentToSpecipifUser("1~That is not a valid option.\nPlease Reenter\n", turn);
            } while (true);

            if (pressed.equals("1")) {
                sentToSpecipifUser("Letter to guess – ", turn);
                guess = getInput(turn);
                System.out.println(CurrentTime() + gameName
                        + " " + list.get(turn).person.getName() + " guessed letter " + guess);
                sentToAllUsersExcept("1~has guessed letter " + guess, turn);
                guess = guess.toUpperCase();
                System.out.print(CurrentTime() + gameName + " is in " + word + " in position(s) ");
                String newasterisk = reCalculate(guess, asterisk, word);
                System.out.println(" Secret word now shows " + newasterisk);
                if (asterisk.equals(newasterisk)) {
                    count--;
                    sentToSpecipifUser("1~The letter ‘" + guess + "’ is not in the secret word.\nSecret Word "
                            + asterisk, turn);
                    sentToAllUsersExcept("1~The letter ‘" + guess + "’ is not in the secret word.", turn);
                    System.out.print(CurrentTime() + gameName + " is not in " + word);
                    System.out.println( " " + gameName + " now has " + count + " guesses remaining");

                } else {
                    asterisk = newasterisk;
                    if(maxNumberOfUsers!=1)
                        sentToSpecipifUser("1~The letter ‘" + guess + "’ is in the secret word.\nSecret Word "
                            + asterisk, turn);
                }

                if (asterisk.equals(word)) {
                    System.out.println(CurrentTime() + gameName
                            + " " + list.get(turn).person.getName() + " guessed letter " + guess
                    +"is correct. other Usernames have lost the game");
                    sentToSpecipifUser("1~That is correct! You win!", turn);
                    sentToAllUsersExcept("1~"+list.get(turn).person.getName() + " guessed the word correctly. You lose! ",
                            turn);
                    
                    SetWon(turn);
                    sentToSpecipifUser("1~Yout Won", turn);
                    break;
                }
            } else if (pressed.equals("2")) {
                sentToSpecipifUser("What is the secret word?", turn);
                guess = getInput(turn);
                guess = guess.toUpperCase();
                if (guess.equals(word)) {
                    sentToSpecipifUser("1~That is correct! You win!", turn);
                    sentToAllUsersExcept("1~"+list.get(turn).person.getName() + " guessed the word "+word+" correctly. You lose! ",
                            turn);
                    System.out.println(CurrentTime() + gameName
                            + " " + list.get(turn).person.getName() + " guessed letter " + guess
                    +"is correct.\n"+list.get(turn).person.getName()+" other Usernames have lost the game");
                    System.out.println(CurrentTime() +list.get(turn).person.getName() + " has guessed the word '" + guess );
                    SetWon(turn);
                    
                    break;
                } else {
                    System.out.println(CurrentTime() + gameName
                            + " " + list.get(turn).person.getName() + " guessed letter " + guess
                    +"is incorrect"+list.get(turn).person.getName()+"has lost and is no longer in the game.");
                    sentToSpecipifUser("1~That is incorrect! You lose!\n"
                            + "The word was '" + word + "'", turn);
                    sentToAllUsersExcept("1~"+list.get(turn).person.getName() + " has guessed the word '" + guess + "'", turn);
                    
                    list.get(turn).person.setLoss(list.get(turn).person.getLoss() + 1);
            
                    
                }
                maxNumberOfUsers--;
                list.remove(turn);
            }
            turn = (++turn) % list.size();

        }
        if (count == 0) {
            SetWon(maxNumberOfUsers);
        }
    }

    String reCalculate(String guess, String asterisk, String word) {
        String newasterisk = "";
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guess.charAt(0)) {
                newasterisk += guess.charAt(0);
                System.out.print(i + " ");
            } else if (asterisk.charAt(i) != '_') {
                newasterisk += word.charAt(i);
            } else {
                newasterisk += "_";
            }
        }
        return newasterisk;
    }

    private void SetWon(int turn) throws IOException {
        //list.get(turn).person.setWin(list.get(turn).person.getWin()+1);
        for (int i = 0; i < list.size(); i++) {
            if (i == turn) {
                list.get(i).person.setWin(list.get(i).person.getWin() + 1);
            } else {
                list.get(i).person.setLoss(list.get(i).person.getLoss() + 1);
            }
            ClientHandler clint = list.get(i);
            Person p = clint.person;
            String line = "1~" + " User " + p.getName() + " is in the game.\n"
                    + p.getName() + "’s Record\n"
                    + "--------------\n"
                    + "Wins – " + p.getWin() + "\n"
                    + "Losses – " + p.getLoss();
            sentToSpecipifUser(line, i);
        }
        for (int i = 0; i < list.size(); i++) {
            ClientHandler clint = list.get(i);
            Person p = clint.person;
            String line = "1~" + " User " + p.getName() + " is in the game.\n"
                    + p.getName() + "’s Record\n"
                    + "--------------\n"
                    + "Wins – " + p.getWin() + "\n"
                    + "Losses – " + p.getLoss();
            sentToAllUsersExcept(line, i);
            Server.dbmanager.UpdatePerson(p);
        }

    }

}
