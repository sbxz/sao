package com.sao.mobile.saolib.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seb on 11/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaoBeacon implements Serializable {
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("macAddress")
    private String macAddress;
    @SerializedName("major")
    private int major;
    @SerializedName("minor")
    private int minor;
    @SerializedName("forOrder")
    private Boolean forOrder;
    @SerializedName("enable")
    private Boolean enable;

    public SaoBeacon() {
    }

    public SaoBeacon(String uuid, String macAddress, int major, int minor, Boolean forOrder, Boolean enable) {
        this.uuid = uuid;
        this.macAddress = macAddress;
        this.major = major;
        this.minor = minor;
        this.forOrder = forOrder;
        this.enable = enable;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public Boolean getForOrder() {
        return forOrder;
    }

    public void setForOrder(Boolean forOrder) {
        this.forOrder = forOrder;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
