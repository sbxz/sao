package com.sao.mobile.saopro.manager;

import android.content.Context;

import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Trader;
import com.sao.mobile.saolib.utils.LocalStore;

/**
 * Created by Seb on 05/01/2017.
 */
public class TraderManager {
    private static TraderManager ourInstance = new TraderManager();

    public static TraderManager getInstance() {
        return ourInstance;
    }

    public Trader trader;
    public Bar currentBar;

    private TraderManager() {
    }

    public String getTraderId(Context context) {
        return LocalStore.readPreferences(context, LocalStore.TRADER_ID);
    }

    public void setTraderId(Context context, Long traderId) {
        LocalStore.writePreferences(context, LocalStore.TRADER_ID, traderId.toString());
    }

    public String getBarId(Context context) {
        return LocalStore.readPreferences(context, LocalStore.TRADER_BAR_ID);
    }

    public void setBarId(Context context, Long barId) {
        LocalStore.writePreferences(context, LocalStore.TRADER_BAR_ID, barId.toString());
    }
}
