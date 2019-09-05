package com.example.mygarden;

public class Sensor {

    private String position;
    private int value;
    private String desc;
    private String image;
    final String TAG = "Sensor.java";

    public Sensor(String position, int value, String image) {
        this.position = position;
        this.value = value;
        this.image = image;
        if(value > 100){
            desc = "No need to water!";
            MainActivity.sendNotification(position);
        }
        else{ desc = "Water me!"; }
    }

    public double getValue() { return value; }

    public void setValue(int value) {
        if(value > 100){
            desc = "No need to water!";
            MainActivity.sendNotification(position);
        }
        else{ desc = "Water me!"; }
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public String getPosition() {
        return position;
    }

    public String getPic(){
        return image;
    }
}
