package com.sao.mobile.saopro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;
import com.sao.mobile.saopro.ui.adapter.BeaconAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconFragment extends BaseFragment {
    private static final String TAG = BeaconFragment.class.getSimpleName();

    private View mView;

    private TextView mNoBeacon;
    private RecyclerView mBeaconRecyclerView;
    private ProgressBar mProgressBar;

    private BeaconAdapter mBeaconAdapter;

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    public BeaconFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_beacon, container, false);
        mBeaconRecyclerView = (RecyclerView) mView.findViewById(R.id.beaconRecyclerView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);
        mNoBeacon = (TextView) mView.findViewById(R.id.noBeacon);

        initRecyclerView();
        retrieveBeaconBar();

        return mView;
    }

    private void initRecyclerView() {
        mBeaconRecyclerView = (RecyclerView) mView.findViewById(R.id.beaconRecyclerView);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mBeaconRecyclerView.setLayoutManager(layoutManager);

        mBeaconAdapter = new BeaconAdapter(mContext, null);
        mBeaconRecyclerView.setAdapter(mBeaconAdapter);
    }

    public void onResume() {
        super.onResume();
        retrieveBeaconBar();
    }

    private void retrieveBeaconBar() {
        showProgressLoad();
        Call<List<SaoBeacon>> barServiceCall = mApiManager.barService.retrieveBeaconBar(mTraderManager.currentBar.getBarId());
        barServiceCall.enqueue(new Callback<List<SaoBeacon>>() {
            @Override
            public void onResponse(Call<List<SaoBeacon>> call, Response<List<SaoBeacon>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve order");
                    return;
                }

                if (response.body() == null || response.body().size() == 0) {
                    hideRecyclerView();
                } else {
                    showRecyclerView();
                }

                Log.i(TAG, "Success retrieve beacon bar");
                mTraderManager.saoBeacons = response.body();
                mBeaconAdapter = new BeaconAdapter(mContext, response.body());
                mBeaconRecyclerView.setAdapter(mBeaconAdapter);
            }

            @Override
            public void onFailure(Call<List<SaoBeacon>> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail retrieve beacon bar.", t);
                SnackBarUtils.showSnackError(getView());
                hideProgressLoad();
            }
        });

    }

    public void updateBeaconList(Intent data) {
    }

    private void showProgressLoad() {
        mProgressBar.setVisibility(View.VISIBLE);
        mBeaconRecyclerView.setVisibility(View.GONE);
        mNoBeacon.setVisibility(View.GONE);
    }

    private void hideProgressLoad() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        mBeaconRecyclerView.setVisibility(View.VISIBLE);
        mNoBeacon.setVisibility(View.GONE);
    }

    private void hideRecyclerView() {
        mBeaconRecyclerView.setVisibility(View.GONE);
        mNoBeacon.setVisibility(View.VISIBLE);
    }
}
