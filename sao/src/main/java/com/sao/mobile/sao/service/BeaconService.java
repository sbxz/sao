package com.sao.mobile.sao.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.Catalog;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.entities.SaoBeacon;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.api.BarService;
import com.sao.mobile.sao.ui.MainActivity;
import com.sao.mobile.saolib.ui.base.BaseService;

import java.util.ArrayList;
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

    private BarService mBarService;

    public BeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void initServices() {
        mBarService = retrofit.create(BarService.class);
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
        //initBeaconScan();
        startBarService();
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
                        detectBar(beacon);
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

        if (mUserManager.currentBeacon == null) {
            return;
        }

        mUserManager.currentBeacon = null;
        mUserManager.currentBar = null;

        Call<Void> barCall = mBarService.leaveBar();
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success leave Bar");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail leave Bar. Message= " + t.getMessage());
            }
        });

        mUserManager.currentBeacon = null;
    }

    private void detectBar(final Beacon beacon) {
        if(mUserManager.currentBeacon != null && mUserManager.currentBeacon.getProximityUUID().equals(beacon.getProximityUUID())) {
            return;
        }

        Call<Void> barCall = mBarService.detectBar();
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success detect bar");
                Bar bar = getFakeBar();

                if(mUserManager.currentBar != null && mUserManager.currentBar.getId().equals(bar.getId())) {
                    return;
                }

                mUserManager.currentBeacon = new SaoBeacon(beacon, "1");
                mUserManager.currentBar = bar;
                startBarService();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail detect Bar. Message= " + t.getMessage());
            }
        });
    }

    private void launchTraderOrder(Beacon beacon) {
        if (mUserManager.currentBeacon == null ||
                mUserManager.currentBeacon.getProximityUUID() != beacon.getProximityUUID() ||
                mOrderManager.order == null) {
            return;
        }

        Call<Void> barCall = mBarService.launchTraderOrder();
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success launch order bar");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail launch order bar. Message= " + t.getMessage());
            }
        });
    }

    private void startBarService() {
        Bar bar = getFakeBar();

       // mUserManager.currentBeacon = new SaoBeacon(beacon, "1");
        mUserManager.currentBar = bar;

        stopService(new Intent(this, BarNotificationService.class));
        startService(new Intent(this, BarNotificationService.class));
    }

    private Bar getFakeBar() {
        List<Product> products = new ArrayList<Product>();
        products.add(new Product("1", "Bière", "Plein de chose", "2"));
        products.add(new Product("2", "Café", "Plein de chose","3"));
        products.add(new Product("3", "Vodka", "Plein de chose","5"));
        products.add(new Product("4", "Grim", "Plein de chose","10"));
        products.add(new Product("5", "Pastis", "Plein de chose","7"));
        products.add(new Product("6", "Ricard", "Plein de chose","6"));
        products.add(new Product("7", "Eau", "Plein de chose","9"));

        List<Product> products1 = new ArrayList<Product>();
        products1.add(new Product("8", "Bière", "Plein de chose", "4"));
        products1.add(new Product("9", "Café", "Plein de chose","5"));
        products1.add(new Product("10", "Vodka", "Plein de chose","7"));
        products1.add(new Product("11", "Grim", "Plein de chose","3"));
        products1.add(new Product("12", "Pastis", "Plein de chose","2"));
        products1.add(new Product("13", "Ricard", "Plein de chose","5"));
        products1.add(new Product("14", "Eau", "Plein de chose","10"));

        List<Product> products2 = new ArrayList<Product>();
        products2.add(new Product("15", "Bière", "Plein de chose", "10"));
        products2.add(new Product("16", "Café", "Plein de chose","5"));
        products2.add(new Product("17", "Vodka", "Plein de chose","8"));
        products2.add(new Product("18", "Grim", "Plein de chose","7"));
        products2.add(new Product("19", "Pastis", "Plein de chose","3"));
        products2.add(new Product("20", "Ricard", "Plein de chose","2"));
        products2.add(new Product("21", "Eau", "Plein de chose","1"));

        List<Product> products3 = new ArrayList<Product>();
        products3.add(new Product("22", "Bière", "Plein de chose", "10"));
        products3.add(new Product("23", "Café", "Plein de chose","5"));
        products3.add(new Product("24", "Vodka", "Plein de chose","8"));
        products3.add(new Product("25", "Grim", "Plein de chose","7"));
        products3.add(new Product("26", "Pastis", "Plein de chose","3"));
        products3.add(new Product("27", "Ricard", "Plein de chose","2"));
        products3.add(new Product("28", "Eau", "Plein de chose","1"));

        List<Product> products4 = new ArrayList<Product>();
        products4.add(new Product("29", "Bière", "Plein de chose", "10"));
        products4.add(new Product("30", "Café", "Plein de chose","5"));
        products4.add(new Product("31", "Vodka", "Plein de chose","8"));
        products4.add(new Product("34", "Grim", "Plein de chose","7"));
        products4.add(new Product("35", "Pastis", "Plein de chose","3"));
        products4.add(new Product("36", "Ricard", "Plein de chose","2"));
        products4.add(new Product("37", "Eau", "Plein de chose","1"));

        List<Product> products5 = new ArrayList<Product>();
        products5.add(new Product("38", "Bière", "Plein de chose", "10"));
        products5.add(new Product("39", "Café", "Plein de chose","5"));
        products5.add(new Product("40", "Vodka", "Plein de chose","8"));
        products5.add(new Product("41", "Grim", "Plein de chose","7"));
        products5.add(new Product("42", "Pastis", "Plein de chose","3"));
        products5.add(new Product("43", "Ricard", "Plein de chose","2"));
        products5.add(new Product("44", "Eau", "Plein de chose","1"));

        List<Product> products6 = new ArrayList<Product>();
        products6.add(new Product("45", "Bière", "Plein de chose", "10"));
        products6.add(new Product("46", "Café", "Plein de chose","5"));
        products6.add(new Product("47", "Vodka", "Plein de chose","8"));
        products6.add(new Product("48", "Grim", "Plein de chose","7"));
        products6.add(new Product("49", "Pastis", "Plein de chose","3"));
        products6.add(new Product("50", "Ricard", "Plein de chose","2"));
        products6.add(new Product("51", "Eau", "Plein de chose","1"));

        List<Catalog> catalog = new ArrayList<Catalog>();
        catalog.add(new Catalog("Les Softs", products));
        catalog.add(new Catalog("Les Klassic", products1));
        catalog.add(new Catalog("Les Supérior", products2));
        catalog.add(new Catalog("Les Bières", products3));
        catalog.add(new Catalog("Les Chaud", products4));
        catalog.add(new Catalog("Cocktails", products5));
        catalog.add(new Catalog("Shooters", products6));

        return new Bar("1", "La kolok", "http://i.imgur.com/CqmBjo5.jpg", "", "", "100", catalog);
    }
}
