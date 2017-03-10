package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanBeaconActivity extends BaseActivity {
    public static final Integer RSSI_THRESHOLD = -60;
    private static final String TAG = ScanBeaconActivity.class.getSimpleName();
    private FrameLayout mSearchBeaconLayout;
    private EditText mInputName;
    private LinearLayout mForOrderLayout;
    private SwitchCompat mForOrderSwitch;
    private Button mSaveButton;
    private LinearLayout mConfigBeaconLayout;

    private TextView mUuid;
    private TextView mMac;
    private TextView mMajor;
    private TextView mMinor;

    private BeaconManager mBeaconManager;
    private Region mRegion;

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    private Boolean mIsConfig = false;

    private Beacon currentBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_beacon);

        setupHeader();
        setupBody();

        initBeaconScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBeaconManager != null) {
            mBeaconManager.stopRanging(mRegion);
            mBeaconManager.disconnect();
        }
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle("Recherche beacon");
    }

    private void setupBody() {
        mSearchBeaconLayout = (FrameLayout) findViewById(R.id.searchBeaconLayout);
        mConfigBeaconLayout = (LinearLayout) findViewById(R.id.configBeaconLayout);

        mForOrderSwitch = (SwitchCompat) findViewById(R.id.forOrderSwitch);
        mForOrderLayout = (LinearLayout) findViewById(R.id.forOrderLayout);
        mForOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForOrderSwitch.setChecked(!mForOrderSwitch.isChecked());
            }
        });

        mInputName = (EditText) findViewById(R.id.input_name);

        mUuid = (TextView) findViewById(R.id.uuid);
        mMac = (TextView) findViewById(R.id.mac);
        mMajor = (TextView) findViewById(R.id.major);
        mMinor = (TextView) findViewById(R.id.minor);

        mSaveButton = (Button) findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                associateBeacon(currentBeacon);
            }
        });
    }

    private void hideSearchLayout() {
        mSearchBeaconLayout.setVisibility(View.GONE);
    }

    private void showSearchLayout() {
        mIsConfig = false;
        mSearchBeaconLayout.setVisibility(View.GONE);
    }

    private void initBeaconScan() {
        mBeaconManager = new BeaconManager(mContext);
        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                if (!beacons.isEmpty()) {
                    beaconsDiscovered(beacons);
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

        if (isMyBeacon(nearBeacon)) {
            return;
        }

        Log.i(TAG, "beacon RSSI: " + nearBeacon.getRssi());
        if (nearBeacon.getRssi() >= RSSI_THRESHOLD) {
            displayConfigBeacon(nearBeacon);
        }
    }

    public boolean equalsBeaconAnSaoBeacon(Beacon beacon, SaoBeacon saoBeacon) {
        return beacon.getProximityUUID().toString().equals(saoBeacon.getUuid()) && beacon.getMajor() == saoBeacon.getMajor() && beacon.getMinor() == saoBeacon.getMinor();
    }

    private boolean isMyBeacon(Beacon nearBeacon) {
        for (SaoBeacon saoBeacon : mTraderManager.saoBeacons) {
            if (equalsBeaconAnSaoBeacon(nearBeacon, saoBeacon)) {
                return true;
            }
        }

        return false;
    }

    private void displayConfigBeacon(Beacon nearBeacon) {
        if (mIsConfig) {
            return;
        }

        currentBeacon = nearBeacon;
        mIsConfig = true;

        mConfigBeaconLayout.setVisibility(View.VISIBLE);

        mUuid.setText(nearBeacon.getProximityUUID().toString());
        mMac.setText(nearBeacon.getMacAddress().toString());
        mMajor.setText(String.valueOf(nearBeacon.getMajor()));
        mMinor.setText(String.valueOf(nearBeacon.getMinor()));

        hideSearchLayout();
    }

    private void associateBeacon(final Beacon beacon) {
        SaoBeacon saoBeacon = new SaoBeacon(beacon.getProximityUUID().toString(), beacon.getMacAddress().toString(), beacon.getMajor(), beacon.getMinor(),
                mForOrderSwitch.isChecked(), true, mInputName.getText().toString());
        Call<Void> barServiceCall = mApiManager.barService.associateBeacon(mTraderManager.currentBar.getBarId(), saoBeacon);
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail associate beacon");
                    Snackbar.make(getView(), "Cette balise est déjà associé à un bar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail associate beacon to bar.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

}
