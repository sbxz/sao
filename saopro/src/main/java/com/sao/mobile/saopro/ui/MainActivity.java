package com.sao.mobile.saopro.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.firebase.iid.FirebaseInstanceId;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.TraderOrder;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;
import com.sao.mobile.saopro.service.SaoMessagingService;
import com.sao.mobile.saopro.ui.activity.AboutActivity;
import com.sao.mobile.saopro.ui.activity.ConditionActivity;
import com.sao.mobile.saopro.ui.activity.LoginActivity;
import com.sao.mobile.saopro.ui.activity.NotificationActivity;
import com.sao.mobile.saopro.ui.activity.OrderDetailsActivity;
import com.sao.mobile.saopro.ui.activity.ProblemActivity;
import com.sao.mobile.saopro.ui.activity.SettingsActivity;
import com.sao.mobile.saopro.ui.fragment.BeaconFragment;
import com.sao.mobile.saopro.ui.fragment.OrderListFragment;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sao.mobile.saopro.ui.adapter.OrderAdapter.REQUEST_CODE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Toolbar mToolbar;
    private Fragment mCurrentFragment;

    private NavigationView mNavigationView;
    private ImageView mBarSelector;
    private ImageView mBarThumbnail;
    private TextView mBarName;

    private Boolean isBarListVisible = false;

    private BroadcastReceiver mBroadcastReceiver;

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NotificationActivity.class);
            }
        });

        setupNavigationView();
        setupMenuListener();
        registerDevice();

        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SaoMessagingService.TYPE_OPEN_ORDER)) {
                    TraderOrder traderOrder = (TraderOrder) intent.getSerializableExtra("traderOrder");
                    showOrderAlert(traderOrder);
                } else if (intent.getAction().equals(SaoMessagingService.TYPE_ORDER_INPROGRESS)) {
                    TraderOrder traderOrder = (TraderOrder) intent.getSerializableExtra("traderOrder");
                    if(mCurrentFragment instanceof  OrderListFragment) {
                        ((OrderListFragment) mCurrentFragment).addOrder(traderOrder);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(SaoMessagingService.TYPE_ORDER_INPROGRESS));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(SaoMessagingService.TYPE_OPEN_ORDER));
    }

    private void showOrderAlert(final TraderOrder traderOrder) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Commande " + mContext.getString(R.string.order_number) + traderOrder.getOrderId());
        builder.setMessage(mContext.getString(R.string.order_alert));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                        intent.putExtra(OrderDetailsActivity.ORDER_EXTRA, traderOrder);
                        ActivityCompat.startActivityForResult(mContext, intent, REQUEST_CODE, null);
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void registerDevice() {
        String traderId = mTraderManager.getTraderId(mContext);

        if(traderId == null) {
            return;
        }

        String deviceId = FirebaseInstanceId.getInstance().getId();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "DeviceId: " + deviceId + "DeviceToken: " + deviceToken);

        Call<Void> deviceCall = mApiManager.traderService.registerDevice(mTraderManager.currentBar.getBarId(), deviceId, deviceToken);
        deviceCall.enqueue(new Callback<Void>() {
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success register device");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail register device.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
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

        Menu menu = mNavigationView.getMenu();
        for (Bar bar : mTraderManager.trader.getBars()) {
            menu.add(R.id.bar_menu, bar.getBarId().intValue(), 0, bar.getName()).setIcon(R.drawable.ic_menu_shop);
        }

        mCurrentFragment = new OrderListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mCurrentFragment);
        fragmentTransaction.commit();
        setTitle(mTraderManager.currentBar.getName());

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
        mBarName = (TextView) headerLayout.findViewById(R.id.bar_name);
        mBarName.setText(mTraderManager.currentBar.getName());

        mBarThumbnail = (ImageView) headerLayout.findViewById(R.id.barThumbnail);
        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.menu_bar_avatar_size);
        Picasso.with(mContext).load(mTraderManager.currentBar.getThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mBarThumbnail);
        mBarThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuView();
            }
        });

        mBarSelector = (ImageView) headerLayout.findViewById(R.id.bar_selector);
        mBarSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeMenuView();
            }
        });
    }

    private void changeMenuView() {
        Menu menu = mNavigationView.getMenu();
        if (!isBarListVisible) {
            mBarSelector.setImageResource(R.drawable.ic_arrow_drop_up_white);
            menu.setGroupVisible(R.id.main_menu, false);
            menu.setGroupVisible(R.id.bar_menu, true);
            menu.findItem(R.id.other_menu).setVisible(false);
        } else {
            mBarSelector.setImageResource(R.drawable.ic_arrow_drop_down_white);
            menu.setGroupVisible(R.id.main_menu, true);
            menu.setGroupVisible(R.id.bar_menu, false);
            menu.findItem(R.id.other_menu).setVisible(true);
        }

        isBarListVisible = !isBarListVisible;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (isBarListVisible) {
            selectNewCurrentBar(id);
            return true;
        }

        if (mCurrentFragment instanceof OrderListFragment) {
            ((OrderListFragment) mCurrentFragment).removeFragment();
        }

        if (id == R.id.nav_home) {
            mCurrentFragment = new OrderListFragment();
            setTitle(mTraderManager.currentBar.getName());
        } else if (id == R.id.nav_beacon) {
            mCurrentFragment = new BeaconFragment();
            setTitle(R.string.menu_beacon);
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

    private void selectNewCurrentBar(int id) {
        mTraderManager.currentBar = mTraderManager.trader.getBars().get(id - 1);
        LocalStore.writePreferences(mContext, LocalStore.TRADER_BAR_ID, mTraderManager.currentBar.getBarId().toString());

        registerDevice();

        changeMenuView();

        View headerLayout = mNavigationView.getHeaderView(0);
        mBarName = (TextView) headerLayout.findViewById(R.id.bar_name);
        mBarName.setText(mTraderManager.currentBar.getName());

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.menu_bar_avatar_size);
        Picasso.with(mContext).load(mTraderManager.currentBar.getThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mBarThumbnail);

        if (mCurrentFragment instanceof OrderListFragment) {
            ((OrderListFragment) mCurrentFragment).removeFragment();

            mCurrentFragment = new OrderListFragment();
            setTitle(mTraderManager.currentBar.getName());

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, mCurrentFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentFragment instanceof OrderListFragment) {
            if (resultCode == Activity.RESULT_OK) {
                ((OrderListFragment) mCurrentFragment).updateOrderList(data);
            }
        } else if (mCurrentFragment instanceof BeaconFragment) {
            if (resultCode == Activity.RESULT_OK) {
                ((BeaconFragment) mCurrentFragment).updateBeaconList(data);
            }
        }
    }

    private void logout() {
        Call<Void> loginCall = mApiManager.traderService.logout(mTraderManager.getTraderId(mContext));
        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success logout");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail logout.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });

        // Clear preferences for next user
        LocalStore.clearPreferences(mContext);

        startActivity(LoginActivity.class);
        finish();
    }
}
