package com.sao.mobile.sao.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 23/01/2017.
 */

public class Bar implements Serializable{
    private String id;
    private String barName;
    private String barThumbnail;
    private String address;
    private String phoneNumber;
    private String point;
    private List<Catalog> catalogs;

    public Bar(String id, String barName, String barThumbnail, String address, String phoneNumber, String point, List<Catalog> catalogs) {
        this.id = id;
        this.barName = barName;
        this.barThumbnail = barThumbnail;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.point = point;
        this.catalogs = catalogs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public String getBarThumbnail() {
        return barThumbnail;
    }

    public void setBarThumbnail(String barThumbnail) {
        this.barThumbnail = barThumbnail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public List<Catalog> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }
}
