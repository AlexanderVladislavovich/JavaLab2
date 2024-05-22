package com.example.lab2;

import java.util.ArrayList;

public class GameModel {
    ArrayList<Point> targets = new ArrayList<>();
    Point BigTarget = null;
    Point SmallTarget = null;
    //ArrayList<Point> bullets = new ArrayList<>();
    Point[] bullets = null;
    //Point userBullet = null;
    //public int[] score = {0,0,0,0};
    Stats stats = new Stats();
    //public int score = 0;

    //String[] stats = new String[4];
    public GameModel(Point bigTarget, Point smallTarget) {
        BigTarget = bigTarget;
        SmallTarget = smallTarget;
        bullets = new Point[4];
    }
    public GameModel(){
        BigTarget = new Point(0,0);
        SmallTarget = new Point(0,0);
        bullets = new Point[4];
        //userBullet = new Point(0, 0);
    }

//    public void setTargets(ArrayList<Point> targets) {
//        this.targets = targets;
//    }
//
    public void TEST_addPoint(Point point) {
        targets.add(point);
        //print();
    }
//    public void moveTargets(int speed) {
//        BigTarget.y = BigTarget.y + speed * 3;
//        if (BigTarget.y >= 300 || BigTarget.y <= 0) {
//            speed1 = -speed1;
//        }
//    }
    public void setBigTarget(int x, int y) {
        BigTarget.x = x;
        BigTarget.y = y;
    }
    public void setSmallTarget(int x, int y) {
        SmallTarget.x = x;
        SmallTarget.y = y;
    }

    public void setBullets(Point[] bullets) {
        this.bullets = bullets;
    }

    public void setBullet(Point p, int id) {
        bullets[id] = p;
    }
    public void moveBullets(int speed, int id) {
//        for (Point p:bullets) {
//            if (p != null) p.x = p.x + 3 * speed;
//        }
        if (bullets[id] != null) {
            bullets[id].x += 3 * speed;
            if (bullets[id].x >= 650) bullets[id] = null;
        }
    }

    public Point[] getBullets() {
        return bullets;
    }
//    public void setUserBullet(int x, int y) {
//        userBullet.x = x;
//        userBullet.y = y;
//    }
//    public void setTargets(Point st, Point bt) {
//        setSmallTarget(st.x, st.y);
//        setBigTarget(bt.x, bt.y);
//    }
    public Point getBigTarget() {
        return BigTarget;
    }
    public Point getSmallTarget() {
        return SmallTarget;
    }
//    public void print() {
//        System.out.println("game model :");
//        for (Point p:targets) {
//            System.out.println(p.PointToString());
//        }
//        System.out.println("\n");
//    }
//    public void setBullets(ArrayList<Point> bullets) {
//        this.bullets = bullets;
//    }
}
