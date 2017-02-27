package com.sao.mobile.sao.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.BeaconService;
import com.sao.mobile.sao.ui.activity.AboutActivity;
import com.sao.mobile.sao.ui.activity.ConditionActivity;
import com.sao.mobile.sao.ui.activity.EditProfileActivity;
import com.sao.mobile.sao.ui.activity.LoginActivity;
import com.sao.mobile.sao.ui.activity.ProblemActivity;
import com.sao.mobile.sao.ui.activity.SettingsActivity;
import com.sao.mobile.sao.ui.fragment.BarsFragment;
import com.sao.mobile.sao.ui.fragment.HomeFragment;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Toolbar mToolbar;
    private Fragment mCurrentFragment;

    private NavigationView mNavigationView;

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setupNavigationView();
        setupMenuListener();

        startServices();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentFragment != null) {
            mCurrentFragment.onResume();
        }
    }

    private void startServices() {
        startService(new Intent(this, BeaconService.class));
    }

    private void setupNavigationView() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);

        mCurrentFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mCurrentFragment);
        fragmentTransaction.commit();
        setTitle(R.string.home_name);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    private void setupMenuListener() {
        View headerLayout = mNavigationView.getHeaderView(0);
        TextView userName = (TextView) headerLayout.findViewById(R.id.user_name);
        userName.setText(mUserManager.currentUser.getName());

        ImageView userThumbnail = (ImageView) headerLayout.findViewById(R.id.userThumbnail);
        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.menu_bar_avatar_size);
        Picasso.with(mContext).load(mUserManager.currentUser.getThumbnail())
                .placeholder(R.drawable.sao)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(userThumbnail);
        userThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(EditProfileActivity.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            mCurrentFragment = new HomeFragment();
            setTitle(R.string.home_name);
        } else if (id == R.id.nav_bar) {
            mCurrentFragment = new BarsFragment();
            setTitle(R.string.menu_bars);
        } else if (id == R.id.nav_settings) {
            startActivity(SettingsActivity.class);
        }  else if (id == R.id.nav_condition) {
            startActivity(ConditionActivity.class);
        } else if (id == R.id.nav_problem) {
            startActivity(ProblemActivity.class);
        } else if (id == R.id.nav_about) {
            startActivity(AboutActivity.class);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        if (mCurrentFragment == null) {
            return false;
        }

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mCurrentFragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // Clear preferences for next user
        LocalStore.clearPreferences(mContext);

        Call<Void> loginCall = mApiManager.loginService.logout();
        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail logout");
                    return;
                }

                Log.i(TAG, "Success logout");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail logout.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });

        startActivity(LoginActivity.class);
        finish();
    }
}
