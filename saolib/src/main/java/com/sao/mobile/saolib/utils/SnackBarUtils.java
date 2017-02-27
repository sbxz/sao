package com.sao.mobile.saolib.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.sao.mobile.saolib.R;

/**
 * Created by SEB on 27/02/2017.
 */

public class SnackBarUtils {
    public static void showSnackError(View view) {
        Snackbar.make(view, R.string.failure_data, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
