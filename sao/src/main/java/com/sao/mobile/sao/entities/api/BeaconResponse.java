package com.sao.mobile.sao.entities.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.SaoBeacon;

import java.io.Serializable;

/**
 * Created by Seb on 26/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeaconResponse implements Serializable {
    @SerializedName("bar")
    private Bar bar;
    @SerializedName("beacon")
    private SaoBeacon saoBeacon;

    public BeaconResponse() {
    }

    public BeaconResponse(Bar bar, SaoBeacon saoBeacon) {
        this.bar = bar;
        this.saoBeacon = saoBeacon;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public SaoBeacon getSaoBeacon() {
        return saoBeacon;
    }

    public void setSaoBeacon(SaoBeacon saoBeacon) {
        this.saoBeacon = saoBeacon;
    }
}
