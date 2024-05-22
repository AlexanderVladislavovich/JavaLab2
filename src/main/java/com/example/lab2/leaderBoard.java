package com.example.lab2;
import javax.persistence.*;



@Entity
@Table
public class leaderBoard {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
    private long Id;
@Column(name = "name")
    private String username;

@Column(name = "num")
    private int winnum;

public leaderBoard(String n, int num){
    username = n;
    winnum = num;
}
public leaderBoard() {}
    public String getUsername() {
        return username;
    }
    public void setUsername(String s) {
        username = s;
    }
    public int getWinnum() {
        return winnum;
    }
    public void setWinnum(int num) {
        winnum = num;
    }
public void print() {
    System.out.println("name="+ username + " winnum=" + winnum);
}
public String print2() {
    return (String) "name="+ username + " winnum=" + winnum;
}
}
