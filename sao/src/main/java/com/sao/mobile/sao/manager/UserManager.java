package com.sao.mobile.sao.manager;

import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.SaoBeacon;
import com.sao.mobile.sao.entities.User;

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
}
