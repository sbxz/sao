package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.service.api.BarService;
import com.sao.mobile.saolib.ui.base.BaseActivity;

public class AboutActivity extends BaseActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    private BarService mBarService;

    @Override
    protected void initServices() {
        mBarService = retrofit.create(BarService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupHeader();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.menu_about));
    }

}
