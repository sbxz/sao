package com.sao.mobile.saolib.entities.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.User;

import java.io.Serializable;

/**
 * Created by Seb on 04/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendBar implements Serializable {
    @SerializedName("bar")
    private Bar bar;
    @SerializedName("friend")
    private User friend;

    public FriendBar() {
    }

    public FriendBar(Bar bar, User friend) {
        this.bar = bar;
        this.friend = friend;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
}
