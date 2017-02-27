package com.sao.mobile.saopro.manager;

import com.sao.mobile.saolib.manager.AbstractApiManager;
import com.sao.mobile.saopro.service.api.BarService;
import com.sao.mobile.saopro.service.api.LoginService;
import com.sao.mobile.saopro.service.api.UserService;

/**
 * Created by SEB on 27/02/2017.
 */
public class ApiManager extends AbstractApiManager {
    private static ApiManager ourInstance = new ApiManager();

    public static ApiManager getInstance() {
        return ourInstance;
    }

    private ApiManager() {
        super();
    }

    public BarService barService;
    public UserService userService;
    public LoginService loginService;

    @Override
    protected void initServices() {
        userService = retrofit.create(UserService.class);
        barService = retrofit.create(BarService.class);
        loginService = retrofit.create(LoginService.class);
    }
}
