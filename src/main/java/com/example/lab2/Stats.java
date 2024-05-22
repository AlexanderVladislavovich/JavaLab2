package com.example.lab2;

public class Stats {
    public String[] names = new String[4];
    public int[] score = new int[4];
    public Stats() {

    }
    public void resetScore() {
        for (int i:score) i = 0;
    }
}
