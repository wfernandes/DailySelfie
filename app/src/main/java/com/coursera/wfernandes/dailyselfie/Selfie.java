package com.coursera.wfernandes.dailyselfie;

public class Selfie {

    private String selfieName;
    private String selfiePath;

    public Selfie(String name){
        this.selfieName = name;
    }

    public String getSelfieName() {
        return selfieName;
    }

    public void setSelfieName(String selfieName) {
        this.selfieName = selfieName;
    }

    public String getSelfiePath() {
        return selfiePath;
    }

    public void setSelfiePath(String selfiePath) {
        this.selfiePath = selfiePath;
    }
}
