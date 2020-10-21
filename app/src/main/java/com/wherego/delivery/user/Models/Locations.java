package com.wherego.delivery.user.Models;

import java.io.Serializable;

/**
 * Created by admin on 11/20/2017.
 */

public class Locations extends Throwable implements Serializable{

    String sAddress;
    String dAddress;
    String sLatitude;
    String dLatitude;
    String sLongitude;
    String dLongitude;
    String description;

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getdAddress() {
        return dAddress;
    }

    public void setdAddress(String dAddress) {
        this.dAddress = dAddress;
    }

    public String getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(String sLatitude) {
        this.sLatitude = sLatitude;
    }

    public String getdLatitude() {
        return dLatitude;
    }

    public void setdLatitude(String dLatitude) {
        this.dLatitude = dLatitude;
    }

    public String getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(String sLongitude) {
        this.sLongitude = sLongitude;
    }

    public String getdLongitude() {
        return dLongitude;
    }

    public void setdLongitude(String dLongitude) {
        this.dLongitude = dLongitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Locations{" +
                "sAddress='" + sAddress + '\'' +
                ", dAddress='" + dAddress + '\'' +
                ", sLatitude='" + sLatitude + '\'' +
                ", dLatitude='" + dLatitude + '\'' +
                ", sLongitude='" + sLongitude + '\'' +
                ", dLongitude='" + dLongitude + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
