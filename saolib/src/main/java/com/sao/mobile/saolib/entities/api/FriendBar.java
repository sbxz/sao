package com.sao.mobile.saolib.entities.api;

import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.User;

/**
 * Created by Seb on 04/03/2017.
 */

public class FriendBar {
    @SerializedName("barVO")
    private Bar bar;
    @SerializedName("friendVO")
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
