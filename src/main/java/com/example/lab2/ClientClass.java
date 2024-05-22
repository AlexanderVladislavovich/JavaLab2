package com.example.lab2;

import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientClass {

    GameModel model = new GameModel();
    //mySocket socket;
    ArrayList<Observer> obs = new ArrayList<>();

    List<leaderBoard> leaderboard = null;
    Gson gson = new Gson();
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    InetAddress ip = null;
    int clientId;
    public boolean isReady = false;
    int port = 1000;
    public int score = 0;
    public int numOfShots = 0;
    public void addObserver(Observer o) {
        obs.add(o);
    }

    public void event1() {
        for (Observer o:obs) {
            o.event1();
        }
    }

    public void event2() {
        for (Observer o:obs) {
            o.event2();
        }
    }
    public void printLeaderboard() {
        for (leaderBoard i:leaderboard) {
            System.out.println(i.print2());
        }

    }
    public String recvResp(){
        String resp;
        try {
            resp = dis.readUTF();
            System.out.println(" recvResp: " + resp);
            Msg m = gson.fromJson(resp, Msg.class);
            if (m.type == TypeAction.END) {
                model.stats = m.stats;
                event2();
                return resp;
            } else if (m.type == TypeAction.STATS) {
                List<leaderBoard> l = m.lb;
                leaderboard = l;
                printLeaderboard();
                return resp;
            }
            model.setBigTarget(m.bigTarget.x, m.bigTarget.y);
            model.setSmallTarget(m.smallTarget.x, m.smallTarget.y);
            model.setBullets(m.bullets);
            score = m.score;
            event1();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    public void pauseRequest(){
        Msg m = new Msg(TypeAction.PAUSE);
        try {
            String str = gson.toJson(m);
            dos.writeUTF(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void continueRequest() {
        Msg m = new Msg(TypeAction.CONTINUE);
        try {
            String str = gson.toJson(m);
            dos.writeUTF(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void readyRequest(String username) {
        Msg m = new Msg(TypeAction.READY, username);
        try {
            String str = gson.toJson(m);
            dos.writeUTF(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void leaderBoardRequest() {
        Msg m = new Msg(TypeAction.STATS);
        try {
            String str = gson.toJson(m);
            dos.writeUTF(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void sendMsg() {

        String str;// = "message from a client";
        try {
            Msg m = new Msg(model.getBullets());
            str = gson.toJson(m);
            dos.writeUTF(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void connect(){
//        InputStream is;
//        OutputStream os;
//        DataInputStream dis;
//        DataOutputStream dos;
        try {
            ip = InetAddress.getLocalHost();
            Socket sc = new Socket(ip, port);
            //socket = new mySocket(sc);
            System.out.println("client connected");


            is = sc.getInputStream();
            os = sc.getOutputStream();

            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            clientId = dis.readInt();

            System.out.println("client sending a message, client id = " + clientId);

            //dos.writeUTF("hello from client");
            new Thread(()->{listen();}).start();
            //String str = dis.readUTF();
            //System.out.println("message from server: " + str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    public void updateModel(ArrayList<Point> points) {
//        model.setTargets(points);
//        event();
//    }
    public void listen() {
        System.out.println("client listen()");
        while (true) {
            recvResp();
        }
    }

    public Point getBigTarget() {
        return model.getBigTarget();
    }
    public Point getSmallTarget() {
        return model.getSmallTarget();
    }
    public Point[] getBullets() {
        //System.out.println("client class get bullets() ");
        return model.getBullets();
    }
//    public void setModel(Point big, Point small) {
//        model.setBigTarget(big.x, big.y);
//        model.setSmallTarget(small.x, small.y);
//    }

    public boolean addBullet(Point p) {
        //model.setUserBullet(p.x, p.y);
        if (model.getBullets()[clientId] == null) {
            model.setBullet(p, clientId);
            sendMsg();
            return true;
        }
    return false;
    }
//    public void TEST_addPoint(Point p) {
//        model.TEST_addPoint(p);
//        event();
//        System.out.println("point added");
//    }
//    public void updateModel(Point point) {
//        System.out.println("Update model");
//        model.addTarget(point);
//        event();
//    }
    public ArrayList<Point> TEST_getData() {
        return model.targets;
    }
}
