package com.example.lab2;

import java.util.ArrayList;
import java.util.List;

public class Msg {
    //public ArrayList<Point> targets;
    public TypeAction type;
    public Point bigTarget = null;
    public Point smallTarget = null;
    public int score;
    //public int numOfShots;
    public String username;
    public Point[] bullets = null;
    public List<leaderBoard> lb = null;

    public Stats stats = null;
//    public Msg(ArrayList<Point> points) {
//        this.targets = points;
//    }
    public Msg(Point[] bullets) {
        type = TypeAction.ADD;
        this.bullets = bullets;
    }
    public Msg(Point p1, Point p2, Point[] b, int score) {
        bigTarget = p1;
        smallTarget = p2;
        bullets = b;
        this.score = score;
        type = TypeAction.ADD;
    }
    public Msg(TypeAction type) {
        this.type = type;
    }
    public Msg(TypeAction type, Stats st) {
        this.type = type;
        this.stats = st;
    }
    public Msg(TypeAction type, String username) {
        this.type = type;
        this.username = username;
    }

    public Msg(List<leaderBoard> leaderboard, TypeAction type) {
        this.lb = leaderboard;
        this.type = type;
    }
}
