package com.example.lab2;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class mainServer implements Observer {

    ArrayList<Game> games = new ArrayList<>();
    int port = 1000;
    InetAddress ip = null;
    static int clientCounter = 0;
    int speed1 = 1, speed2 = 2;
    Thread serverThread = null;
    Thread waitingThread = null;

    leaderBoard leaderBoard = new leaderBoard("eee", 50);
    static boolean isReady[] = new boolean[4];

    static public Stats stats = new Stats();
    GameModel model;
    public void startServer() {
        ServerSocket ss;
        Socket sc;
        model = new GameModel(new Point(350, 100), new Point(450, 100));
        try {
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 0, ip);
            System.out.println("server started");
            waitingThread = new Thread(() -> { waitForClients();});
            waitingThread.start();
            while (clientCounter <= 4) {
                sc = ss.accept();
                System.out.println("connected " + sc.getPort());

                Game g = new Game(model, sc, clientCounter++, this, stats.names[clientCounter]);
                games.add(g);

            }
        } catch (IOException ex) {
            System.out.println("server error");
        }
        System.out.println("client counter = " + clientCounter);
    }

    public void waitForClients() {
        System.out.println("waiting....");
        while(true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (serverThread == null & IsAllReady()) {
                serverThread = new Thread(() -> {
                    StartGame();
                });
                serverThread.start();
                return;
            }
        }
    }
    public void updateDB(leaderBoard lb) {
        Session s = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t1 = s.beginTransaction();
        String q = "from leaderBoard where name = :name";
        leaderBoard l = s.createQuery(q, leaderBoard.class)
                .setParameter("name", lb.getUsername()).uniqueResult();
         t1.commit();
         t1.begin();
        if (l == null) {
            System.out.println("SAVE BOARD");
            s.save(lb);
        }
        else {
            l.setWinnum(l.getWinnum() + 1);
            s.save(l);
        }

        t1.commit();
        s.close();
    }
    public void saveToDB() {
        Session s = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t1 = s.beginTransaction();
        s.save(leaderBoard);
        t1.commit();
        s.close();
        System.out.println("SAVED SUCCESSFULLY");
    }
    public void getFromDB() {
        Session s = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t1 = s.beginTransaction();
        leaderBoard l = s.createQuery("from leaderBoard where name = 'aaa' ",
                com.example.lab2.leaderBoard.class).uniqueResult();
        t1.commit();
        s.close();
        l.print();
    }
    public static  boolean IsAllReady() {
        if (clientCounter == 0) return false;
        for (int i = 0; i < clientCounter; i++) {
            if (!isReady[i]) return false;
        }
        return true;
    }

    public static List<leaderBoard> getLeaderBoardList() {
        Session s = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t1 = s.beginTransaction();
        Query query = s.createQuery("FROM leaderBoard", com.example.lab2.leaderBoard.class);
        List<leaderBoard> lb = query.getResultList();
        t1.commit();
        s.close();
        return lb;
    }
    public void  StartGame() {
        System.out.println("StartGame()");
        while ( clientCounter > 0 ) {
            synchronized (this) {
                moveTargets();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
                break;
            }
        }
        System.out.println("server game ended");
    }
    public void moveTargets() {
        model.setBigTarget(model.getBigTarget().x, model.getBigTarget().y + speed1 * 3);
        if (model.getBigTarget().y >= 300 || model.getBigTarget().y <= 0) {
            speed1 = -speed1;
        }

        model.setSmallTarget(model.getSmallTarget().x, model.getSmallTarget().y + speed2 * 3);
        if (model.getSmallTarget().y >= 300 || model.getSmallTarget().y <= 0) {
            speed2 = -speed2;
        }
    }
    public static void main(String[] args) {
        mainServer serv = new mainServer();
        serv.startServer();
    }

    public leaderBoard getWinner() {
        leaderBoard l = new leaderBoard();
        l.setWinnum(1);
        for (int ind = 0; ind < 4; ind++) {
            if (stats.score[ind] >= 6 ) {
                l.setUsername(stats.names[ind]);
            }

        }
        return l;
    }
    @Override
    public void event1() {
        serverThread.interrupt();
        serverThread = null;
        for (Game g:games) {
            g.stopGame();
        }
        leaderBoard l = getWinner();
        updateDB(l);
        stats.resetScore();

        if (!waitingThread.isAlive()) {
            waitingThread = new Thread(() -> waitForClients());
            waitingThread.start();
        }
    }

    @Override
    public void event2() {

    }
}