package com.sao.mobile.saolib.utils;

import android.util.Log;

/**
 * Created by SEB on 27/02/2017.
 */

public class LoggerUtils {
    public static void apiFail(String tag, String message, Throwable throwable) {
        Log.e(tag, message + " Message= " + throwable.getMessage() + " Cause= " + throwable.getCause());
    }
}
