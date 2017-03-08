package com.sao.mobile.sao.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.saolib.NotificationConstants;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.api.BeaconResponse;
import com.sao.mobile.saolib.ui.base.BaseService;
import com.sao.mobile.saolib.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconService extends BaseService {
    public static final Integer RSSI_THRESHOLD = -60;
    public static final Integer LEAVE_RSSI = -90;
    private static final String TAG = BeaconService.class.getSimpleName();
    private BeaconManager mBeaconManager;
    private Region mRegion;

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();

    private ApiManager mApiManager = ApiManager.getInstance();

    private List<Beacon> mBlackBeacon;

    public BeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "on startCommand");
        mBlackBeacon = new ArrayList<>();
        initBeaconScan();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create");
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
                    if (!isAvailableBeacon(beacon)) {
                        return;
                    }

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

        Call<Void> barCall = mApiManager.barService.leaveBar(mUserManager.getFacebookUserId());
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

        Call<BeaconResponse> barCall = mApiManager.barService.scanBeacon(mUserManager.getFacebookUserId(), beacon.getProximityUUID().toString(), beacon.getMajor(), beacon.getMinor());
        barCall.enqueue(new Callback<BeaconResponse>() {
            @Override
            public void onResponse(Call<BeaconResponse> call, Response<BeaconResponse> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Beacon not found uuid= " + beacon.getProximityUUID().toString());
                    putBeaconBlackList(beacon);
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

    private Boolean isAvailableBeacon(Beacon beacon) {
        if (mBlackBeacon.size() == 0) {
            return true;
        }

        for(Beacon b : mBlackBeacon) {
            if(b.getProximityUUID().toString().equals(beacon.getProximityUUID().toString())) {
                return true;
            }
        }

        return false;
    }

    private void putBeaconBlackList(Beacon beacon) {
        mBlackBeacon.add(beacon);
    }

    private void launchTraderOrder(Beacon beacon) {
        if (mUserManager.currentBeacon == null ||
                !mUserManager.currentBeacon.getUuid().equals(beacon.getProximityUUID().toString()) ||
                mOrderManager.order == null) {
            return;
        }

        if(!mOrderManager.order.getStep().equals(Order.Step.READY)) {
            return;
        }

        Intent intent = new Intent(NotificationConstants.TYPE_OPEN_ORDER);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private void startBarService() {
        stopService(new Intent(this, BarNotificationService.class));
        startService(new Intent(this, BarNotificationService.class));
    }
}
