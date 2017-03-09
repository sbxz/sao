package com.sao.mobile.saolib.entities.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.SaoBeacon;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 26/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeaconResponse implements Serializable {
    @SerializedName("bar")
    private Bar bar;
    @SerializedName("beacon")
    private List<SaoBeacon> saoBeacons;

    public BeaconResponse() {
    }

    public BeaconResponse(Bar bar, List<SaoBeacon> saoBeacons) {
        this.bar = bar;
        this.saoBeacons = saoBeacons;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public List<SaoBeacon> getSaoBeacons() {
        return saoBeacons;
    }

    public void setSaoBeacons(List<SaoBeacon> saoBeacon) {
        this.saoBeacons = saoBeacon;
    }
}
