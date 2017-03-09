package com.sao.mobile.sao.manager;

import android.content.Context;

import com.estimote.sdk.Beacon;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.sao.mobile.sao.entities.CardPayment;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saolib.entities.User;
import com.sao.mobile.saolib.utils.LocalStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sao.mobile.saolib.utils.LocalStore.CARD_NUMBER_KEY;

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

    public List<CardPayment> getCardPayments(Context context) {
        Set<String> keySets = LocalStore.readSetPreferences(context, CARD_NUMBER_KEY);

        if (keySets == null) {
            return null;
        }

        List<CardPayment> cardPayments = new ArrayList<>();
        Set<String> cardSets;
        List<String> cardList;
        for (String key : keySets) {
            cardList = new ArrayList<>();
            cardSets = LocalStore.readSetPreferences(context, key);
            for (String card : cardSets) {
                cardList.add(card);
            }
            cardPayments.add(new CardPayment(cardList));
        }

        return cardPayments;
    }
}


