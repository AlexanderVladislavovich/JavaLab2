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
    //Point smallTarget = null;
    ///Point bigTarget = null;
    static int clientCounter = 0;
    int speed1 = 1, speed2 = 2;
    Thread serverThread = null;
    Thread waitingThread = null;
    boolean connected = false;
    leaderBoard leaderBoard = new leaderBoard("eee", 50);
    static boolean isReady[] = new boolean[4];
    static String[] users = new String[4];

    static public Stats stats = new Stats();
    GameModel model;// = new GameModel();
    public void startServer() {
        //saveToDB();
        //getFromDB();
        //updateDB();
        ServerSocket ss;
        Socket sc;
        System.out.println(" ______ start server" + Thread.currentThread().getName());
        model = new GameModel(new Point(350, 100), new Point(450, 100));
//        model.addTarget(new Point(250,100));
//        model.addTarget(new Point(350,100));
//        InputStream is;
//        OutputStream os;
//        DataInputStream dis;
//        DataOutputStream dos;
        try {
            //while (true)
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 0, ip);
            System.out.println("server started");
            //new Thread(() -> {StartGame(); }).start();
            waitingThread = new Thread(() -> { waitForClients();
//                while(true) {
//                    System.out.println("waiting ---------");
//                    for (boolean i:isReady) {
//                        if (i == true) System.out.print("true ");
//                        else System.out.print("false ");
//                    }
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    //System.out.println(isReady[0], isReady[1], isReady[2] ,isReady[3]);
//                    if (serverThread == null & IsAllReady()) {
//                        System.out.println("IS ALL READY:" + IsAllReady());
//                        serverThread = new Thread(() -> {
//                            StartGame();
//                        });
//                        serverThread.start();
//                        return;
//                    }
//                }
            });
            waitingThread.start();
            while (clientCounter <= 4) {
                sc = ss.accept();
                System.out.println("connected " + sc.getPort());

                Game g = new Game(model, sc, clientCounter++, this, stats.names[clientCounter]);
                games.add(g);
                //Thread th = new Thread;
//                if (serverThread == null & IsAllReady())  {
//                    serverThread = new Thread(() -> {StartGame(); });
//                    serverThread.start();
//                }
//                System.out.println("IS ALL READY:" + IsAllReady());
//                connected = true;
//                new Thread(()->{g.run();}).start();
//                new Thread(()->{g.listen();}).start();
//                is = sc.getInputStream();
//                os = sc.getOutputStream();
//
//                dis = new DataInputStream(is);
//                dos = new DataOutputStream(os);
//
//                String s = dis.readUTF();
//
//                System.out.println("connection: " + s);
//                dos.writeUTF("hello from server");
            }
        } catch (IOException ex) {
            System.out.println("server error");
        }
        System.out.println("client counter = " + clientCounter);
    }

    public void waitForClients() {
        while(true) {
            System.out.println("waiting ---------");
            System.out.println("IS ALL READY1:" + IsAllReady());
            for (boolean i:isReady) {
                if (i == true) System.out.print("true ");
                else System.out.print("false ");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println(isReady[0], isReady[1], isReady[2] ,isReady[3]);
            if (serverThread == null & IsAllReady()) {
                System.out.println("IS ALL READY2:" + IsAllReady());
                serverThread = new Thread(() -> {
                    System.out.println("----START GAME--------------------------------------");
                    StartGame();
                });
                serverThread.start();
                System.out.println("RETURN ____");
                return;
            }
        }
    }
    public void updateDB(leaderBoard lb) {
        Session s = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t1 = s.beginTransaction();
        String q = "from leaderBoard where name = :name";// + leaderBoard.getUsername();
//        List<leaderBoard> l = s.createQuery(q, leaderBoard.class).list();
        leaderBoard l = s.createQuery(q, leaderBoard.class)
                .setParameter("name", lb.getUsername()).uniqueResult();
        //l.print();
         t1.commit();
         t1.begin();
        if (l == null) {
            System.out.println("SAVE BOARD");
            s.save(lb);
        }
        else {
            //leaderBoard l2 = s.createQuery(q, com.example.lab2.leaderBoard.class).uniqueResult();
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

        while ( clientCounter > 0 ) {
            System.out.println("____ StartGame " + Thread.currentThread().getName());
            synchronized (this) {
                moveTargets();
                System.out.println(" big x: "+model.getBigTarget().x+ " big y: " + model.getBigTarget().y);
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
        //model.delTargets();
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
            System.out.println("score  = " + stats.score[ind]);
            if (stats.score[ind] >= 6 ) {
                l.setUsername(stats.names[ind]);

                System.out.println("Name  = " + stats.names[ind]);
            }

        }
        return l;
    }
    @Override
    public void event1() {
        System.out.println("______EVENT1");
        serverThread.interrupt();
        serverThread = null;

        leaderBoard l = getWinner();
        updateDB(l);
        //System.out.println("wait for clients");
        stats.resetScore();
        for (Game g:games) {
            //System.out.println("1_____for: " + Thread.currentThread().getName() +"Is interrupted: " + Thread.currentThread().isInterrupted());
            //new Thread(() ->{g.stopGame();});
            g.stopGame();
        }
        //System.out.println("3________" + Thread.currentThread().getName()+"Is interrupted: " + Thread.currentThread().isInterrupted());
        if (!waitingThread.isAlive()) {
            waitingThread = new Thread(() -> waitForClients());
            waitingThread.start();
        }
        //System.out.println(users[0]);
    }

    @Override
    public void event2() {

    }
}