package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

public class BarInfoActivity extends BaseActivity {
    private static final String TAG = BarInfoActivity.class.getSimpleName();
    private ImageView mBarThumbnail;
    private TextView mBarNom;
    private TextView mBarAdress;
    private TextView mBarTime;

    public static final String BAR_EXTRA = "barExtra";
    private Bar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);

        mBar = (Bar) getIntent().getSerializableExtra(BAR_EXTRA);

        setupHeader();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bar_info_title));

        mBarThumbnail = (ImageView) findViewById(R.id.barThumbnail);
        Picasso.with(mContext).load(mBar.getThumbnail()).fit().centerCrop().into(mBarThumbnail);

        mBarNom = (TextView) findViewById(R.id.barName);
        mBarNom.setText(mBar.getName());
        mBarTime = (TextView) findViewById(R.id.barTime);
        mBarTime.setText(mBar.getDescription());
        mBarAdress = (TextView) findViewById(R.id.barAdress);
        mBarAdress.setText(mBar.getAddress());

    }
}
