package com.sao.mobile.sao.manager;

import com.sao.mobile.sao.entities.Bar;

/**
 * Created by Seb on 23/01/2017.
 */
public class UserManager {

    public String userName;
    public String userThumbnail;
    public Bar currentBar;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }
}
