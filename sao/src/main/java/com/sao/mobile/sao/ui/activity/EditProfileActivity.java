package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.saolib.ui.base.BaseActivity;

public class EditProfileActivity extends BaseActivity {

    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupHeader();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(mUserManager.currentUser.getName());
    }
}
