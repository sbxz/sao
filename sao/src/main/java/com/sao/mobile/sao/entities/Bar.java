package com.sao.mobile.sao.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Seb on 23/01/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bar implements Serializable{
    @SerializedName("barId")
    private Long barId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("address")
    private String address;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("catalog")
    private Map<String, List<Product>> catalog;

    public Bar() {
    }

    public Long getBarId() {
        return barId;
    }

    public void setBarId(Long barId) {
        this.barId = barId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, List<Product>> getCatalog() {
        return catalog;
    }

    public void setCatalog(Map<String, List<Product>> catalog) {
        this.catalog = catalog;
    }
}
