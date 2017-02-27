package com.sao.mobile.saopro.manager;

import com.sao.mobile.saopro.entities.Bar;

import java.util.List;

/**
 * Created by Seb on 05/01/2017.
 */
public class UserManager {
    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    public List<Bar> bars;
    public Bar currentBar;

    private UserManager() {
    }
}
