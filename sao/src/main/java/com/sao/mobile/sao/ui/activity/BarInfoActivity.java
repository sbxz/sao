package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.ui.base.BaseActivity;

public class BarInfoActivity extends BaseActivity {
    private static final String TAG = BarInfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);

        setupHeader();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bar_info_title));
    }
}
