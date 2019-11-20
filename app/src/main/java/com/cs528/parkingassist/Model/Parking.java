package com.cs528.parkingassist.Model;

public class Parking {
    private int id;
    private String maker;
    private String model;
    private String color;
    private String licence;
    private String description;
    private long lat;
    private long lon;
    private String time;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getMaker() {
        return maker;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public long getLat() {
        return lat;
    }

    public long getLon() {
        return lon;
    }

    public String getTime() {
        return time;
    }

    public Parking(int id, String make, String model, String color, String licence, long lat, long lon, String time) {
        this.id = id;
        this.maker = make;
        this.model = model;
        this.color = color;
        this.licence = licence;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }
}
