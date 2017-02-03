package com.sao.mobile.saopro.entities;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.MacAddress;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Seb on 14/01/2017.
 */

public class SaoBeacon extends Beacon implements Serializable{
    private String id;

    public SaoBeacon(String id, UUID proximityUUID, MacAddress macAddress, int major, int minor, int measuredPower, int rssi) {
        super(proximityUUID, macAddress, major, minor, measuredPower, rssi);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
