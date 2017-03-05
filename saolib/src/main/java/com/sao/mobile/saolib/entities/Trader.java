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
    @SerializedName("traderId")
    private Long traderId;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("bars")
    private List<Bar> bars;

    public Trader() {
    }

    public Trader(Long traderId, String lastName, String firstName, List<Bar> bars) {
        this.traderId = traderId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.bars = bars;
    }

    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
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
