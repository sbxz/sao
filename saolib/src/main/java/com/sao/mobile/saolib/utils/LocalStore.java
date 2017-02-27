package com.sao.mobile.saolib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Seb on 01/12/2016.
 */

public class LocalStore {
    public static final String SESSION_ID = "session_id";

    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_TOKEN = "devive_token";

    public static final String CURRENT_BAR_ID = "current_bar_id";

    public static String readPreferences(Context context, String key) {
        Activity activity = (Activity) context;

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public static void writePreferences(Context context, String key, String value) {
        Activity activity = (Activity) context;

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void removePreferences(Context context, String key) {
        Activity activity = (Activity) context;

        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void clearPreferences(Context context) {
        Activity activity = (Activity) context;
        activity.getPreferences(Context.MODE_PRIVATE).edit().clear().commit();
    }
}
