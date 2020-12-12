package com.example.ninjagoldgame;

public class Model {

    private String name;
    private int score;

    public Model(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public Model() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
