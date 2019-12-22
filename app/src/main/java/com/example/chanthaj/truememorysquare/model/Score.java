package com.example.chanthaj.truememorysquare.model;

public class Score {
    private long id;
    private int score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getScoreString() {
        return Integer.toString(score);
    }

    public void setScore(int score) {
        this.score = score;
    }



}