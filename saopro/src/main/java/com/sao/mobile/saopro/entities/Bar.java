package com.sao.mobile.saopro.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seb on 29/11/2016.
 */

public class Bar implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("address")
    private String address;
    @SerializedName("phoneNumber")
    private String phoneNumber;

    public Bar(String id, String name, String thumbnail, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public void setPphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
