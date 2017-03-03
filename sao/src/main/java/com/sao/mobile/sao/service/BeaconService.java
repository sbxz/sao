package com.sao.mobile.sao.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.api.BeaconResponse;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.saolib.ui.base.BaseService;
import com.sao.mobile.saolib.utils.LoggerUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconService extends BaseService {
    private static final String TAG = BeaconService.class.getSimpleName();

    public static final Integer RSSI_THRESHOLD = -50;
    public static final Integer LEAVE_RSSI = -70;

    private BeaconManager mBeaconManager;
    private Region mRegion;

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();

    private ApiManager mApiManager = ApiManager.getInstance();

    public BeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "on startCommand");
        //initBeaconScan();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create");
        //startBarService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on destroy");
    }

    private void initBeaconScan() {
        Log.i(TAG, "initBeaconScan");
        mBeaconManager = new BeaconManager(getApplicationContext());
        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);
                    Log.i(TAG, "beacon RSSI: " + beacon.getRssi());

                    if (beacon.getRssi() > RSSI_THRESHOLD) {
                        launchTraderOrder(beacon);
                    }

                    if (beacon.getRssi() < LEAVE_RSSI) {
                        leaveBar();
                    } else {
                        scanBeacon(beacon);
                    }
                } else {
                    leaveBar();
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

    private void leaveBar() {
        Log.i(TAG, "leaveBar");
        stopService(new Intent(this, BarNotificationService.class));

        if (mUserManager.currentBeacon == null || mUserManager.currentBar == null) {
            return;
        }

        Call<Void> barCall = mApiManager.barService.leaveBar(mUserManager.currentBar.getBarId());
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail leave");
                    return;
                }

                Log.i(TAG, "Success leave Bar");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail leave Bar.", t);
            }
        });

        mUserManager.currentBeacon = null;
        mUserManager.currentBar = null;
    }

    private void scanBeacon(final Beacon beacon) {
        if (mUserManager.currentBeacon != null && mUserManager.currentBeacon.getUuid().equals(beacon.getProximityUUID().toString())) {
            return;
        }

        Call<BeaconResponse> barCall = mApiManager.barService.scanBeacon(beacon.getProximityUUID().toString(), beacon.getMajor(), beacon.getMinor());
        barCall.enqueue(new Callback<BeaconResponse>() {
            @Override
            public void onResponse(Call<BeaconResponse> call, Response<BeaconResponse> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Beacon not found");
                    return;
                }

                Log.i(TAG, "Success detect bar");

                Bar bar = response.body().getBar();
                mUserManager.currentBeacon = response.body().getSaoBeacon();

                if (mUserManager.currentBar != null && mUserManager.currentBar.getBarId().equals(bar.getBarId())) {
                    return;
                }

                mUserManager.currentBar = bar;
                startBarService();
            }

            @Override
            public void onFailure(Call<BeaconResponse> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail detect Bar.", t);
            }
        });
    }

    private void launchTraderOrder(Beacon beacon) {
        if (mUserManager.currentBeacon == null ||
                !mUserManager.currentBeacon.getUuid().equals(beacon.getProximityUUID().toString()) ||
                mOrderManager.order == null) {
            return;
        }

        Call<Order> barCall = mApiManager.barService.startOrder(mOrderManager.order);
        barCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail launch order");
                    return;
                }

                Log.i(TAG, "Success launch order bar");
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e(TAG, "Fail launch order bar. Message= " + t.getMessage());
            }
        });
    }

    private void startBarService() {
        stopService(new Intent(this, BarNotificationService.class));
        startService(new Intent(this, BarNotificationService.class));
    }
}
