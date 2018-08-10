package com.example.chillysoup.searchplace;

import java.io.Serializable;

public class NearbyModel implements Serializable{

//    private static final long serialVersionUID = 1L;


    private String name;
    private String icon;
    private String address;
    private String place_id;
    private Double lat;
    private Double lng;

    public NearbyModel(String name, String icon, String address, String place_id, Double lat, Double lng) {
        this.name = name;
        this.icon = icon;
        this.address = address;
        this.place_id = place_id;
        this.lat = lat;
        this.lng = lng;


    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getAddress() {
        return address;
    }

    public String getPlace_id() {
        return place_id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}