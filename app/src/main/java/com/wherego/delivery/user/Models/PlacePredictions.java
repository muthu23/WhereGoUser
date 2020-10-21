package com.wherego.delivery.user.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kyra on 1/11/2016.
 */
public class PlacePredictions implements Serializable{

    public String strSourceLatitude = "";
    public String strSourceLongitude = "";
    public String strSourceLatLng = "";
    public String strSourceAddress = "";

    public String strDestLatitude = "";
    public String strDestLongitude = "";
    public String strDestLatLng = "";
    public String strDestAddress = "";


    public ArrayList<PlaceAutoComplete> getPlaces() {
        return predictions;
    }

    public void setPlaces(ArrayList<PlaceAutoComplete> places) {
        this.predictions = places;
    }

    private ArrayList<PlaceAutoComplete> predictions;


    public String getStrSourceLatitude() {
        return strSourceLatitude;
    }

    public void setStrSourceLatitude(String strSourceLatitude) {
        this.strSourceLatitude = strSourceLatitude;
    }

    public String getStrSourceLongitude() {
        return strSourceLongitude;
    }

    public void setStrSourceLongitude(String strSourceLongitude) {
        this.strSourceLongitude = strSourceLongitude;
    }

    public String getStrSourceLatLng() {
        return strSourceLatLng;
    }

    public void setStrSourceLatLng(String strSourceLatLng) {
        this.strSourceLatLng = strSourceLatLng;
    }

    public String getStrSourceAddress() {
        return strSourceAddress;
    }

    public void setStrSourceAddress(String strSourceAddress) {
        this.strSourceAddress = strSourceAddress;
    }

    public String getStrDestLatitude() {
        return strDestLatitude;
    }

    public void setStrDestLatitude(String strDestLatitude) {
        this.strDestLatitude = strDestLatitude;
    }

    public String getStrDestLongitude() {
        return strDestLongitude;
    }

    public void setStrDestLongitude(String strDestLongitude) {
        this.strDestLongitude = strDestLongitude;
    }

    public String getStrDestLatLng() {
        return strDestLatLng;
    }

    public void setStrDestLatLng(String strDestLatLng) {
        this.strDestLatLng = strDestLatLng;
    }

    public String getStrDestAddress() {
        return strDestAddress;
    }

    public void setStrDestAddress(String strDestAddress) {
        this.strDestAddress = strDestAddress;
    }
}
