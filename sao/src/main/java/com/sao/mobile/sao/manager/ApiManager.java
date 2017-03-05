package com.sao.mobile.sao.manager;

import com.sao.mobile.sao.service.api.BarService;
import com.sao.mobile.sao.service.api.UserService;
import com.sao.mobile.saolib.manager.AbstractApiManager;

/**
 * Created by SEB on 27/02/2017.
 */
public class ApiManager extends AbstractApiManager {
    private static ApiManager ourInstance = new ApiManager();

    public static ApiManager getInstance() {
        return ourInstance;
    }

    public UserService userService;
    public BarService barService;

    private ApiManager() {
        super();
    }

    @Override
    protected void initServices() {
        userService = retrofit.create(UserService.class);
        barService = retrofit.create(BarService.class);
    }
}
