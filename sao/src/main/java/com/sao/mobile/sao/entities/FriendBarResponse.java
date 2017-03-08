package com.sao.mobile.sao.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.User;

import java.io.Serializable;

/**
 * Created by Seb on 06/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendBarResponse implements Serializable {
    @SerializedName("bar")
    private Bar bar;

    @SerializedName("friend")
    private User friend;

    public FriendBarResponse() {
    }

    public FriendBarResponse(Bar bar, User friend) {
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
