package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saopro.R;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private LinearLayout mNotifLayout;
    private LinearLayout mSoundLayout;
    private SwitchCompat mSoundSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupHeader();

        setupNotifLayout();
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

    private void setupNotifLayout() {
        mNotifLayout = (LinearLayout) findViewById(R.id.notifLayout);
        mNotifLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(NotificationSettingActivity.class);
            }
        });

    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.menu_settings));
    }

}
