package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeaconInfoActivity extends BaseActivity {
    public static final String BEACON_EXTRA = "beacon_extra";
    private static final String TAG = BeaconInfoActivity.class.getSimpleName();
    private Button mDeleteButton;

    private TextView mUuid;
    private TextView mMac;
    private TextView mMajor;
    private TextView mMinor;

    private EditText mInputName;
    private LinearLayout mForOrderLayout;
    private SwitchCompat mForOrderSwitch;

    private SaoBeacon mBeacon;

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        mBeacon = (SaoBeacon) getIntent().getSerializableExtra(BEACON_EXTRA);

        setupHeader();
        setupBody();
    }

    private void setupBody() {
        mInputName = (EditText) findViewById(R.id.input_name);
        mInputName.setText(mBeacon.getName());

        mForOrderSwitch = (SwitchCompat) findViewById(R.id.forOrderSwitch);
        mForOrderLayout = (LinearLayout) findViewById(R.id.forOrderLayout);
        mForOrderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForOrderSwitch.setChecked(!mForOrderSwitch.isChecked());
            }
        });
        mForOrderSwitch.setChecked(mBeacon.getForOrder());

        mUuid = (TextView) findViewById(R.id.uuid);
        mUuid.setText(mBeacon.getUuid());
        mMac = (TextView) findViewById(R.id.mac);
        mMac.setText(mBeacon.getMacAddress());
        mMajor = (TextView) findViewById(R.id.major);
        mMajor.setText(String.valueOf(mBeacon.getMajor()));
        mMinor = (TextView) findViewById(R.id.minor);
        mMinor.setText(String.valueOf(mBeacon.getMinor()));

        mDeleteButton = (Button) findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBeacon();
            }
        });
    }

    private void deleteBeacon() {
        showProgressDialog("Suppréssion en cours");
        Call<Void> barServiceCall = mApiManager.barService.deleteBeacon(mTraderManager.currentBar.getBarId(), mBeacon.getUuid(), mBeacon.getMajor(), mBeacon.getMinor());
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                hideProgressDialog();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail delete beacon");
                    SnackBarUtils.showSnackError(getView());
                    return;
                }

                finish();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail delete beacon bar.", t);
                SnackBarUtils.showSnackError(getView());
                hideProgressDialog();
            }
        });
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(mBeacon.getName());
    }
}
