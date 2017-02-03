package com.sao.mobile.sao.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.ui.base.BaseFragment;


public class HomeFragment extends BaseFragment {


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void initServices() {

    }
}
