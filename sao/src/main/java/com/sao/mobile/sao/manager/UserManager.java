package com.sao.mobile.sao.manager;

import com.estimote.sdk.Beacon;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saolib.entities.User;

import java.util.List;

/**
 * Created by Seb on 23/01/2017.
 */
public class UserManager {

    private static UserManager ourInstance = new UserManager();
    public User currentUser;
    public Bar currentBar;
    public SaoBeacon currentBeacon;
    public List<SaoBeacon> saoBeacons;

    private UserManager() {
    }

    public static UserManager getInstance() {
        return ourInstance;
    }

    public String getFacebookUserId() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null) {
            return null;
        }

        return accessToken.getUserId();
    }

    public String getName() {
        Profile profile = Profile.getCurrentProfile();
        if(profile == null) {
            return null;
        }

        return profile.getName();
    }

    public String getThumbnail() {
        Profile profile = Profile.getCurrentProfile();
        if(profile == null) {
            return null;
        }

        return profile.getProfilePictureUri(100,100).toString();
    }

    public SaoBeacon getBeacon(Beacon nearBeacon) {
        if (saoBeacons == null) {
            return null;
        }

        for (SaoBeacon saoBeacon : saoBeacons) {
            if (equalsBeaconAnSaoBeacon(nearBeacon, saoBeacon)) {
                return saoBeacon;
            }
        }

        return null;
    }

    public boolean equalsBeacon(Beacon beacon1, Beacon beacon2) {
        return beacon2.getProximityUUID().toString().equals(beacon1.getProximityUUID().toString()) && beacon2.getMajor() == beacon1.getMajor() && beacon2.getMinor() == beacon1.getMinor();
    }

    public boolean equalsBeaconAnSaoBeacon(Beacon beacon, SaoBeacon saoBeacon) {
        return beacon.getProximityUUID().toString().equals(saoBeacon.getUuid()) && beacon.getMajor() == saoBeacon.getMajor() && beacon.getMinor() == saoBeacon.getMinor();
    }

    public void logout() {
        currentUser = null;
        currentBar = null;
        currentBeacon = null;
        saoBeacons = null;
    }
}
