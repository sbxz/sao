package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.ui.base.BaseActivity;

public class FriendBarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_bar);

        setupHeader();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bar_friend_title));
    }
}
