package com.sao.mobile.saolib.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 27/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trader implements Serializable{
    @SerializedName("name")
    private String lastName;
    @SerializedName("name")
    private String firstName;
    @SerializedName("bars")
    private List<Bar> bars;

    public Trader() {
    }

    public Trader(String lastName, String firstName, List<Bar> bars) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.bars = bars;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<Bar> getBars() {
        return bars;
    }

    public void setBars(List<Bar> bars) {
        this.bars = bars;
    }
}
