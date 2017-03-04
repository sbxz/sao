package com.sao.mobile.saolib.ui.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Seb on 11/02/2017.
 */

public abstract class BaseService extends Service {

    protected Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
