package com.sao.mobile.sao.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seb on 25/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("thumbnail")
    private String thumbnail;

    public User() {

    }

    public User(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
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
}
