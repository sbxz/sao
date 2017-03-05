package com.sao.mobile.saopro.manager;

import com.sao.mobile.saolib.manager.AbstractApiManager;
import com.sao.mobile.saopro.service.api.BarService;
import com.sao.mobile.saopro.service.api.TraderService;

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
    public TraderService traderService;

    @Override
    protected void initServices() {
        traderService = retrofit.create(TraderService.class);
        barService = retrofit.create(BarService.class);
    }
}
