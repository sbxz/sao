package com.sao.mobile.sao.entities;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Seb on 04/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookResult {
    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("name")
    private String name;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("token")
    private String token;

    @SerializedName("facebookUserId")
    private String facebookUserId;

    @SerializedName("expires")
    private long expires;

    @SerializedName("lastRefresh")
    private long lastRefresh;

    public FacebookResult() {
    }

    public FacebookResult(LoginResult loginResult, Profile profile) {
        if(profile == null) {
            return;
        }

        this.name = profile.getName();
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
        this.thumbnail = profile.getProfilePictureUri(100,100).toString();

        AccessToken accessToken = loginResult.getAccessToken();
        this.token = accessToken.getToken();
        this.facebookUserId = accessToken.getUserId();
        this.expires = accessToken.getExpires().getTime();
        this.lastRefresh = accessToken.getLastRefresh().getTime();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }
}
