package com.dugan.restartlogger;

/**
 * Created by Todd on 4/25/2015.
 */
public class BootObjects {
    private String bootText;
    private int bootIconId;

    public BootObjects(String bootText, int bootIconId){
        this.bootText = bootText;
        this.bootIconId = bootIconId;
    }

    public int getBootId(){
        return this.bootIconId;
    }

    public String getBootText(){
        return this.bootText;
    }
}
