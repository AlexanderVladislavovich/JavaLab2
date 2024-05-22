package com.example.lab2;

import java.util.ArrayList;

public class GameModel {
    ArrayList<Point> targets = new ArrayList<>();
    Point BigTarget = null;
    Point SmallTarget = null;
    Point[] bullets = null;
    Stats stats = new Stats();
    public GameModel(Point bigTarget, Point smallTarget) {
        BigTarget = bigTarget;
        SmallTarget = smallTarget;
        bullets = new Point[4];
    }
    public GameModel(){
        BigTarget = new Point(0,0);
        SmallTarget = new Point(0,0);
        bullets = new Point[4];
    }

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
        if (bullets[id] != null) {
            bullets[id].x += 3 * speed;
            if (bullets[id].x >= 650) bullets[id] = null;
        }
    }

    public Point[] getBullets() {
        return bullets;
    }
    public Point getBigTarget() {
        return BigTarget;
    }
    public Point getSmallTarget() {
        return SmallTarget;
    }

}
