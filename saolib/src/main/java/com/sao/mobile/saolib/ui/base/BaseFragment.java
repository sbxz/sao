package com.sao.mobile.saolib.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.sao.mobile.saolib.R;

/**
 * Created by Seb on 03/08/2016.
 */
public abstract class BaseFragment extends Fragment {

    protected BaseActivity mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof BaseActivity) {
            mContext = (BaseActivity) context;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = (BaseActivity) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onResumed() {

    }

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    protected void showSnackError() {
        Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void logApiError(String tag, String message, Throwable t) {
        Log.e(tag, message + " Message= " + t.getMessage() + " Cause= " + t.getCause());
    }
}
