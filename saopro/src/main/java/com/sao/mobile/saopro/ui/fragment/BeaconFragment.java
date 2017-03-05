package com.sao.mobile.saopro.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconFragment extends BaseFragment {
    private static final String TAG = BeaconFragment.class.getSimpleName();

    public static final Integer RSSI_THRESHOLD = -60;

    private View mView;

    private TextView mBeaconText;
    private RecyclerView mBeaconRecyclerView;
    private ProgressBar mProgressBar;

    private BeaconAdapter mBeaconAdapter;

    private BeaconManager mBeaconManager;
    private Region mRegion;

    private List<String> mBeaconList;
    private List<String> mBeaconLists;

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

        mBeaconList = new ArrayList<>();

        initRecyclerView();
        initBeaconScan();
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

                Log.i(TAG, "Success retrieve beacon bar");
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

    private void initBeaconScan() {
        mBeaconManager = new BeaconManager(mContext);
        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);

                    if(beacon.getRssi() > RSSI_THRESHOLD) {
                        associateBeacon(beacon);
                    }
                }
            }
        });

        mRegion = new Region("ranged region", null, null, null);

        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                mBeaconManager.startRanging(mRegion);
            }
        });
    }

    private void associateBeacon(final Beacon beacon) {
        SaoBeacon saoBeacon = new SaoBeacon(beacon.getProximityUUID().toString(), beacon.getMacAddress().toString(), beacon.getMajor(), beacon.getMinor(), false, true);
        Call<Void> barServiceCall = mApiManager.barService.associateBeacon(mTraderManager.currentBar.getBarId() ,saoBeacon);
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve order");
                    return;
                }

                Log.i(TAG, "Success save beacon");
                Snackbar.make(mView, R.string.beacon_success_save, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail associate beacon to bar.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    public void updateBeaconList(Intent data) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBeaconManager != null) {
            mBeaconManager.stopRanging(mRegion);
        }
    }

    private void showProgressLoad() {
        mProgressBar.setVisibility(View.VISIBLE);
        mBeaconRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProgressLoad() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mBeaconRecyclerView.setVisibility(View.VISIBLE);
    }
}
