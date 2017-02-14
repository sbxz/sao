package com.sao.mobile.sao.manager;

import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.SaoBeacon;

/**
 * Created by Seb on 23/01/2017.
 */
public class UserManager {

    public String userName;
    public String userThumbnail;
    public Bar currentBar;
    public SaoBeacon currentBeacon;

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }
}
