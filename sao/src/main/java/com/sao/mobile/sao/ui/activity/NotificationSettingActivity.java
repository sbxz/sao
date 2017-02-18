package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.ui.base.BaseActivity;

public class NotificationSettingActivity extends BaseActivity {
    private static final String TAG = NotificationSettingActivity.class.getSimpleName();

    private LinearLayout mVibrationLayout;
    private SwitchCompat mVibrationSwitch;
    private LinearLayout mLedLayout;
    private SwitchCompat mLedSwitch;
    private LinearLayout mSoundLayout;
    private SwitchCompat mSoundSwitch;
    @Override
    protected void initServices() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        setupHeader();

        setupVibrationLayout();
        setupLedLayout();
        setupSoundLayout();
    }

    private void setupSoundLayout() {
        mSoundSwitch = (SwitchCompat) findViewById(R.id.soundSwitch);
        mSoundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSoundLayout = (LinearLayout) findViewById(R.id.soundLayout);
        mSoundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundSwitch.setChecked(!mSoundSwitch.isChecked());
            }
        });
    }

    private void setupLedLayout() {
        mLedSwitch = (SwitchCompat) findViewById(R.id.ledSwitch);
        mLedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLedLayout = (LinearLayout) findViewById(R.id.ledLayout);
        mLedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLedSwitch.setChecked(!mLedSwitch.isChecked());
            }
        });
    }

    private void setupVibrationLayout() {
        mVibrationSwitch = (SwitchCompat) findViewById(R.id.vibrationSwitch);
        mVibrationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mVibrationLayout = (LinearLayout) findViewById(R.id.vibrationLayout);
        mVibrationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrationSwitch.setChecked(!mVibrationSwitch.isChecked());
            }
        });
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notification_title));
    }

}
