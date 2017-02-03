package com.sao.mobile.sao.manager;

/**
 * Created by Seb on 23/01/2017.
 */
public class UserManager {

    public String userName;
    public String userThumbnail;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }
}
