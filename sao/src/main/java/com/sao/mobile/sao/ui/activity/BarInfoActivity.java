package com.sao.mobile.sao.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.sao.mobile.sao.ui.activity.BarActivity.IMAGE_TRANSITION_NAME;

public class BarInfoActivity extends BaseActivity {
    public static final String BAR_EXTRA = "barExtra";
    private static final String TAG = BarInfoActivity.class.getSimpleName();
    private ImageView mBarThumbnail;
    private TextView mBarName;
    private TextView mBarDetails;
    private TextView mBarAddress;
    private TextView mBarScheduled;

    private Bar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        setStatusBarTranslucent(true);

        mBar = (Bar) getIntent().getSerializableExtra(BAR_EXTRA);

        setupHeader();
        setupBody();
        setupMap();
    }

    private void setupHeader() {
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), IMAGE_TRANSITION_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle("");

        mBarThumbnail = (ImageView) findViewById(R.id.barThumbnail);
        Picasso.with(mContext).load(mBar.getThumbnail()).fit().centerCrop().into(mBarThumbnail, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) mBarThumbnail.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    private void setupBody() {
        mBarName = (TextView) findViewById(R.id.barName);
        mBarName.setText(mBar.getName());

        mBarDetails = (TextView) findViewById(R.id.barDetails);
        mBarDetails.setText(mBar.getDescription());

        mBarScheduled = (TextView) findViewById(R.id.barSheduled);
        mBarScheduled.setText("   " + mBar.getSchedule());

        mBarAddress = (TextView) findViewById(R.id.barAddress);
        mBarAddress.setText("   " + mBar.getAddress());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(mBar.getLatitude(), mBar.getLongitude());

                googleMap.addMarker(new MarkerOptions().title(mBar.getName()).position(latLng));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(primary);
        collapsingToolbarLayout.setStatusBarScrimColor(primary);
        supportStartPostponedEnterTransition();
    }
}
