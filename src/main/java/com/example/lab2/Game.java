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
        listenThread = new Thread(()->{listen();});
        listenThread.start();
    }
    public void listen() {
        while(true) {
            String m = readMsg();
        }
    }
    public String readMsg() {
        String str;
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
                mainServer.isReady[clientId] = true;
                mainServer.stats.names[clientId] = name;
                if (gameThread == null) {
                    gameThread = new Thread(() -> {
                        while (true) {
                            System.out.println("wait for ready");
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
    public void  stopGame() {
        gameThread.interrupt();
        gameThread = null;

        try {
            Msg m = new Msg(TypeAction.END, mainServer.stats);
            String str = gson.toJson(m);
            dos.writeUTF(str);
            mainServer.isReady[clientId] = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void moveBullets() {
        model.moveBullets(3, clientId);
    }

    public boolean collisionCheck() {
        Point[] points = model.getBullets();
        Point p = null;
        if (points != null)  p = model.getBullets()[clientId];

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
                }
                else if ((dx2 * dx2 + dy2 * dy2) <= (rad2 + rad3)*(rad2 + rad3)) {
                    score += 2;
                    mainServer.stats.score[clientId] += 2;
                    model.setBullet(null, clientId);
                }
            }
            if (score >= 6) {
                score = 0;
                Thread t = new Thread(() -> {
                    obs.event1();} );
                t.start();
                return true;
            }
        return false;
    }

    public void run() {
        System.out.println("Game::run()");
        while (true) {
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
                synchronized (this) {
                    moveBullets();
                    collisionCheck();
                }
                Msg m = new Msg(model.getBigTarget(), model.getSmallTarget(), model.getBullets(), score);
                String json = gson.toJson(m);
                dos.writeUTF(json);
                System.out.println("___\n");
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println("game stopped");
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("thread ended");
    }

}
