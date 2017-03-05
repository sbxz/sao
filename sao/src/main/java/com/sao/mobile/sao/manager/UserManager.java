package com.sao.mobile.sao.manager;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saolib.entities.User;

/**
 * Created by Seb on 23/01/2017.
 */
public class UserManager {

    public User currentUser;
    public Bar currentBar;
    public SaoBeacon currentBeacon;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
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
}
