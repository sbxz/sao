package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.service.api.BarService;

public class BeaconInfoActivity extends BaseActivity {
    private static final String TAG = BeaconInfoActivity.class.getSimpleName();

    private BarService mBarService;

    protected void initServices() {
        mBarService = retrofit.create(BarService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_info);

        setupHeader();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notification_title));
    }
}
