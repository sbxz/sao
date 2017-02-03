package com.sao.mobile.saolib.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.sao.mobile.saolib.SaoConstants;

import retrofit2.Retrofit;

/**
 * Created by Seb on 03/08/2016.
 */
public abstract class BaseFragment extends Fragment {

    protected BaseActivity mContext;

    public Retrofit retrofit;

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
        initRetrofit();
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

    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    /**
     * Initialize retrofit for api call
     */
    public void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(SaoConstants.BASE_URL)
                .build();

        initServices();
    }

    protected abstract void initServices();


}
