package com.laizhenghuo.media;

public class LrcMusic {
    private int time;
    private String lrc;

    public LrcMusic() {
    }

    public LrcMusic(int time, String lrc) {
        this.time = time;
        this.lrc = lrc;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }
}