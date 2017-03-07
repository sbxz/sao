package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

public class BarInfoActivity extends BaseActivity {
    private static final String TAG = BarInfoActivity.class.getSimpleName();
    private ImageView mBarThumbnail;
    private TextView mBarNom;
    private TextView mbarDetails;
    private TextView mBarAdress;
    private TextView mBarTime;

    static final LatLng PARIS = new LatLng(48.858093, 2.294694);

    public static final String BAR_EXTRA = "barExtra";
    private Bar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        mBar = (Bar) getIntent().getSerializableExtra(BAR_EXTRA);

        setupHeader();
        setupFooter();
        setupMap();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(mBar.getName());

        mBarThumbnail = (ImageView) findViewById(R.id.barThumbnail);
        Picasso.with(mContext).load(mBar.getThumbnail()).fit().centerCrop().into(mBarThumbnail);
    }

    private void setupFooter() {
        mbarDetails = (TextView) findViewById(R.id.barDetails);
        mbarDetails.setText(mBar.getDescription());
        mBarTime = (TextView) findViewById(R.id.barTime);
        mBarTime.setText(" Horaire : 9h / 18h");
        mBarAdress = (TextView) findViewById(R.id.barAdress);
        mBarAdress.setText(mBar.getAddress());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.addMarker(new MarkerOptions().title("Paris").position(PARIS));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS, 15));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        });
    }
}
