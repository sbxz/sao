package com.sao.mobile.sao.service;

import android.app.ActivityManager;
import android.content.Context;
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
import com.sao.mobile.sao.ui.fragment.HomeFragment;
import com.sao.mobile.saolib.NotificationConstants;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.SaoBeacon;
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
    public static final Integer LEAVE_RSSI = -80;
    private static final String TAG = BeaconService.class.getSimpleName();
    private BeaconManager mBeaconManager;
    private Region mRegion;

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    private List<Beacon> mBlackBeacons;

    private Boolean isScanning = false;
    private Boolean isLeaving = false;

    public BeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "on startCommand");
        mBlackBeacons = new ArrayList<>();
        //initBeaconScan();
        startBarService();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create");
    }

    @Override
    public void onDestroy() {
        if (mBeaconManager != null) {
            mBeaconManager.disconnect();
        }

        super.onDestroy();
        Log.i(TAG, "on destroy");
    }

    private void initBeaconScan() {
        Log.i(TAG, "initBeaconScan");
        mBeaconManager = new BeaconManager(getApplicationContext());
        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                if (!beacons.isEmpty()) {
                    beaconsDiscovered(beacons);
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

    private void beaconsDiscovered(List<Beacon> beacons) {
        Beacon nearBeacon = beacons.get(0);

        SaoBeacon newCurrentBeacon = mUserManager.getBeacon(nearBeacon);
        if (newCurrentBeacon != null) {
            mUserManager.currentBeacon = newCurrentBeacon;

            if (isMyServiceRunning(BarNotificationService.class) && newCurrentBeacon.getForOrder() && nearBeacon.getRssi() >= RSSI_THRESHOLD) {
                launchTraderOrder(nearBeacon);
            } else if (nearBeacon.getRssi() <= LEAVE_RSSI) {
                leaveBar();
            }

            return;
        }

        if (mBlackBeacons.size() != 0 && !isAvailableBeacon(nearBeacon)) {
            return;
        }

        Log.i(TAG, "beacon RSSI: " + nearBeacon.getRssi());
        if (nearBeacon.getRssi() >= LEAVE_RSSI) {
            scanBeacon(nearBeacon);
        }
    }

    private void scanBeacon(final Beacon beacon) {
        if (isScanning) {
            return;
        }

        isScanning = true;
        Call<BeaconResponse> barCall = mApiManager.barService.scanBeacon(mUserManager.getFacebookUserId(), beacon.getProximityUUID().toString(), beacon.getMajor(), beacon.getMinor());
        barCall.enqueue(new Callback<BeaconResponse>() {
            @Override
            public void onResponse(Call<BeaconResponse> call, Response<BeaconResponse> response) {
                isScanning = false;
                if (response.code() != 200) {
                    Log.i(TAG, "Beacon not found uuid= " + beacon.getProximityUUID().toString() + " major= " + beacon.getMajor() + " minor= " + beacon.getMinor());
                    putBeaconBlackList(beacon);
                    return;
                }

                if (response.body().getBar() == null) {
                    return;
                }

                Log.i(TAG, "Success detect bar");
                mUserManager.saoBeacons = response.body().getSaoBeacons();
                mUserManager.currentBar = response.body().getBar();
                mOrderManager.removeOrder();
                startBarService();
            }

            @Override
            public void onFailure(Call<BeaconResponse> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail detect Bar.", t);
                isScanning = false;
            }
        });
    }

    private void leaveBar() {
        Log.i(TAG, "leaveBar");
        stopService(new Intent(this, BarNotificationService.class));

        if (mUserManager.currentBeacon == null || mUserManager.currentBar == null) {
            return;
        }

        if (isLeaving) {
            return;
        }

        isLeaving = true;
        Call<Void> barCall = mApiManager.barService.leaveBar(mUserManager.getFacebookUserId());
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLeaving = false;
                if (response.code() != 200) {
                    Log.i(TAG, "Fail leave");
                    return;
                }

                Log.i(TAG, "Success leave Bar");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail leave Bar.", t);
                isLeaving = false;
            }
        });

        Intent intent = new Intent(HomeFragment.UPDATE_CURRENT_BAR);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        mUserManager.saoBeacons = null;
        mUserManager.currentBeacon = null;
        mUserManager.currentBar = null;
    }

    private Boolean isAvailableBeacon(Beacon beacon) {
        if (mBlackBeacons.size() == 0) {
            return false;
        }

        for (Beacon b : mBlackBeacons) {
            if (mUserManager.equalsBeacon(b, beacon)) {
                return false;
            }
        }

        return true;
    }

    private void putBeaconBlackList(Beacon beacon) {
        Boolean isFind = false;
        for (Beacon b : mBlackBeacons) {
            if (mUserManager.equalsBeacon(b, beacon)) {
                isFind = true;
            }
        }

        if (!isFind) {
            mBlackBeacons.add(beacon);
        }
    }

    private void launchTraderOrder(Beacon nearBeacon) {
        if (mUserManager.currentBeacon == null ||
                !mUserManager.currentBeacon.getUuid().equals(nearBeacon.getProximityUUID().toString()) ||
                !mUserManager.currentBeacon.getForOrder() ||
                mOrderManager.order == null ||
                !mOrderManager.order.getStep().equals(Order.Step.READY)) {
            return;
        }

        Intent intent = new Intent(NotificationConstants.TYPE_OPEN_ORDER);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }

    private void startBarService() {
        mUserManager.currentBar = new Bar((long) 1, "La kolok", "Bar convivial, doté d'un billard et de jeux, proposant des bières en libre-service et une petite restauration.", "30 Avenue Berthelot, 69007 Lyon", "https://s3-eu-west-1.amazonaws.com/sao-thumbnail/bar/bar1.jpg", "04 72 71 31 77", (double) 45.7464333, (double) 4.8353712, "18h / 2h");

        if (mUserManager.currentBar == null) {
            return;
        }

        stopService(new Intent(this, BarNotificationService.class));
        startService(new Intent(this, BarNotificationService.class));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
