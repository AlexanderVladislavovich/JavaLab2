package com.example.lab2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Game {

    Observer obs;
    GameModel model;
    Socket sock;
    Gson gson = new Gson();
    InputStream is;
    OutputStream os;
    DataOutputStream dos;
    DataInputStream dis;
    int clientId;
    Thread gameThread = null;
    Thread listenThread = null;

    boolean isPaused = false;
    String username;
    int score = 0;

    int speed1 = 1, speed2 = 2;

    public Game(Socket s){
        sock = s;
    }
    public Game(GameModel model, Socket socket, int id, Observer o, String n) {
        this.model = model;
        sock = socket;
        clientId = id;
        obs = o;
        username = n;
        try {
            is = sock.getInputStream();
            os = sock.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            dos.writeInt(clientId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        gameThread = new Thread(()->{run();});
//        gameThread.start();
        listenThread = new Thread(()->{listen();});
        System.out.println("NEW THREAD LISTEN THREAD:" + listenThread.getName());
        listenThread.start();
    }

//    public void connect() {
//        while(true) {
//            System.out.println("----connect()");
//            accept();
//            //Point[] b = gson.fromJson(m, Msg.class).bullets;
//        }
//    }
//    public void accept() {
//        String str;
//        try {
//            str = dis.readUTF();
//            Msg m = gson.fromJson(str, Msg.class);
//            if (m.type == TypeAction.CONNECT) {
//
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
    public void listen() {
        while(true) {
            System.out.println("----listen()");
            String m = readMsg();
            //Point[] b = gson.fromJson(m, Msg.class).bullets;
            System.out.println(m);
        }
    }
    public String readMsg() {
        //Msg resp = null;
        String str;// = null;
        System.out.println("-----readMsg()");
        try {
            str = dis.readUTF();System.out.println(str);
            Msg m = gson.fromJson(str, Msg.class);
            if (m.type == TypeAction.STATS) {
                Msg msg = new Msg(mainServer.getLeaderBoardList(), TypeAction.STATS);
                String strMsg = gson.toJson(msg, Msg.class);
                dos.writeUTF(strMsg);
            }
            else if (m.type == TypeAction.READY) {
                String name = m.username;
                username = name;
                //mainServer.users[clientId] = name;
                mainServer.isReady[clientId] = true;
                mainServer.stats.names[clientId] = name;
                if (gameThread == null) {
                    gameThread = new Thread(() -> {
                        while (true) {
                            if (mainServer.IsAllReady()) {
                                run();
                                break;
                            }
                        }
                    });
                    gameThread.start();
                }
                return str;
            }
            else if (m.type == TypeAction.PAUSE) {
                isPaused = true;
                return str;
            }
            else if (m.type == TypeAction.CONTINUE) {
                isPaused = false;
                synchronized (this) {
                    notifyAll();
                }
                return str;
            }
            Point[] b = m.bullets;
            model.setBullets(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str;
    }

//    public void sendMsg() {
//
//    }

//
//    public void moveTargets() {
//        //model.delTargets();
//        model.setBigTarget(model.getBigTarget().x, model.getBigTarget().y + speed1 * 3);
//        if (model.getBigTarget().y >= 300 || model.getBigTarget().y <= 0) {
//            speed1 = -speed1;
//        }
//
//        model.setSmallTarget(model.getSmallTarget().x, model.getSmallTarget().y + speed2 * 3);
//        if (model.getSmallTarget().y >= 300 || model.getSmallTarget().y <= 0) {
//            speed2 = -speed2;
//        }
//    }
    public void  stopGame() {
        gameThread.interrupt();
        gameThread = null;

        System.out.println("2________" + Thread.currentThread().getName() +"Is interrupted: " + Thread.currentThread().isInterrupted());
//        try {
//            gameThread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        try {
            Msg m = new Msg(TypeAction.END, mainServer.stats);
            String str = gson.toJson(m);
            dos.writeUTF(str);
            mainServer.isReady[clientId] = false;
            score = 0;
            //mainServer.stats.score[clientId] = 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //dos.writeUTF("");
    }
    public void moveBullets() {
        model.moveBullets(3, clientId);
    }

    public boolean collisionCheck() {
        //for (Point p : model.getBullets()) {
        Point p = model.getBullets()[clientId];
            if (p != null) {
                double dx = model.getBigTarget().x - p.x;
                double dy = model.getBigTarget().y - p.y;
                double rad1 = 20, rad2 = 10, rad3 = 2;
                double dx2 = model.getSmallTarget().x - p.x;
                double dy2 = model.getSmallTarget().y - p.y;
                if ((dx * dx + dy * dy) <= (rad1 + rad3)*(rad1 + rad3)) {
                    mainServer.stats.score[clientId] += 1;
                    score += 1;
                    model.setBullet(null, clientId);
                    //return;
                }
                else if ((dx2 * dx2 + dy2 * dy2) <= (rad2 + rad3)*(rad2 + rad3)) {
                    score += 2;
                    mainServer.stats.score[clientId] += 2;
                    model.setBullet(null, clientId);
                    //return;
                }
            }
            //if (mainServer.stats.score[clientId] >= 6) {
            if (score >= 6) {
                //System.out.println("NEW NAMELESS THREAD SCORE = 6");
                Thread t = new Thread(() -> {
                    //System.out.println("NEW THREAD: --------------");
                    obs.event1();} );
                t.start();
                return true;
//                gameThread.interrupt();
//                gameThread = null;
            }
       // }
        return false;
    }

    public void run() {
        System.out.println("Game::run()");
        while (true) {
            System.out.println("___-__" + Thread.currentThread().getName()+" Is interrupted: " + Thread.currentThread().isInterrupted());
            if (isPaused) {
                synchronized (this) {
                    try {
                        System.out.println("Thread is paused");
                        this.wait();
                        System.out.println("Thread is resumed");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                //readMsg();
                synchronized (this) {
                    //moveTargets();
                    moveBullets();
                    collisionCheck();
                }
                System.out.println("score = " + mainServer.stats.score[clientId]);
                Msg m = new Msg(model.getBigTarget(), model.getSmallTarget(), model.getBullets(), score);
                String json = gson.toJson(m);
                dos.writeUTF(json);
                System.out.println("___\n");
                Thread.sleep(20);
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
                System.out.println("game stopped");
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("thread ended");
    }

}
