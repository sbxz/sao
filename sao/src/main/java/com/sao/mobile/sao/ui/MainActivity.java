package com.sao.mobile.sao.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.BarNotificationService;
import com.sao.mobile.sao.service.BeaconService;
import com.sao.mobile.sao.ui.activity.AboutActivity;
import com.sao.mobile.sao.ui.activity.ConditionActivity;
import com.sao.mobile.sao.ui.activity.EditProfileActivity;
import com.sao.mobile.sao.ui.activity.LoginActivity;
import com.sao.mobile.sao.ui.activity.PaymentActivity;
import com.sao.mobile.sao.ui.activity.ProblemActivity;
import com.sao.mobile.sao.ui.activity.SettingsActivity;
import com.sao.mobile.sao.ui.adapter.FriendAdapter;
import com.sao.mobile.sao.ui.fragment.BarsFragment;
import com.sao.mobile.sao.ui.fragment.ConsumptionsFragment;
import com.sao.mobile.sao.ui.fragment.HomeFragment;
import com.sao.mobile.saolib.NotificationConstants;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.api.FriendBar;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int DEFAULT_SIZE_PAGE = 8;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Toolbar mToolbar;
    private Fragment mCurrentFragment;

    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawer;

    private UserManager mUserManager = UserManager.getInstance();
    private OrderManager mOrderManager = OrderManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    private BroadcastReceiver mBroadcastReceiver;

    private Boolean mAlertVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setupRecyclerView();
        setupNavigationView();
        setupMenuListener();

        startServices();
        registerBroadcastReceiver();
        getCurrentOrder();
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.friendRecycler);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(this));
        mRecyclerView.setLayoutManager(layoutManager);
        mFriendAdapter = new FriendAdapter(mContext, null);
        mRecyclerView.setAdapter(mFriendAdapter);
    }


    private void refreshFriendList() {
        Call<List<FriendBar>> friendCall = mApiManager.userService.retrieveFriendBar(mUserManager.getFacebookUserId());
        friendCall.enqueue(new Callback<List<FriendBar>>() {
            @Override
            public void onResponse(Call<List<FriendBar>> call, Response<List<FriendBar>> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve friend bar");
                    return;
                }

                Log.i(TAG, "Success retrieve friend bar");
                mFriendAdapter.addListItem(response.body());
            }

            @Override
            public void onFailure(Call<List<FriendBar>> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail retrieve friend bar.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentFragment != null) {
            mCurrentFragment.onResume();
        }

        refreshFriendList();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_openRight) {
            mDrawer.openDrawer(Gravity.END);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(HomeFragment.UPDATE_CURRENT_BAR)
                        || intent.getAction().equals(NotificationConstants.TYPE_ORDER_READY)
                        || intent.getAction().equals(NotificationConstants.TYPE_ORDER_VALIDATE)) {
                    if (mCurrentFragment.isVisible() && mCurrentFragment instanceof HomeFragment) {
                        ((HomeFragment) mCurrentFragment).setupCurrentBar();
                    }
                } else if (intent.getAction().equals(NotificationConstants.TYPE_OPEN_ORDER)) {
                    displayTraderAlert();
                } else if (intent.getAction().equals(NotificationConstants.TYPE_BAR_NEWS)) {
                    News news = (News) intent.getSerializableExtra(NotificationConstants.TYPE_BAR_NEWS);
                    if (mCurrentFragment.isVisible() && mCurrentFragment instanceof HomeFragment) {
                        ((HomeFragment) mCurrentFragment).addNews(news);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(HomeFragment.UPDATE_CURRENT_BAR));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(NotificationConstants.TYPE_ORDER_VALIDATE));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(NotificationConstants.TYPE_ORDER_READY));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(NotificationConstants.TYPE_OPEN_ORDER));
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(NotificationConstants.TYPE_BAR_NEWS));
    }

    private void displayTraderAlert() {
        if (mAlertVisible) {
            return;
        }

        mAlertVisible = true;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Valider la commande?");
        builder.setMessage("Envoie une alerte au bar");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callValidateTraderOrder();
                        mAlertVisible = false;
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertVisible = false;
                    }
                });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void callValidateTraderOrder() {
        Call<Void> barCall = mApiManager.barService.orderBeacon(mUserManager.getFacebookUserId(), mUserManager.currentBeacon.getUuid(), mUserManager.currentBeacon.getMajor(), mUserManager.currentBeacon.getMinor());
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail launch order");
                    return;
                }

                Log.i(TAG, "Success launch order bar");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail launch order bar. Message= " + t.getMessage());
            }
        });
    }

    private void startServices() {
        stopService(new Intent(this, BeaconService.class));
        startService(new Intent(this, BeaconService.class));
    }

    private void setupNavigationView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);

        mCurrentFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mCurrentFragment);
        fragmentTransaction.commit();
        setTitle(R.string.home_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                /*if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                }*/
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
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawer(GravityCompat.END);
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
        } else if (id == R.id.nav_consumption) {
            mCurrentFragment = new ConsumptionsFragment();
            setTitle(R.string.menu_consumption);
        } else if (id == R.id.nav_payment) {
            startActivity(PaymentActivity.class);
        } else if (id == R.id.nav_settings) {
            startActivity(SettingsActivity.class);
        } else if (id == R.id.nav_condition) {
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

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        // Clear preferences for next user
        LocalStore.clearPreferences(mContext);

        Call<Void> loginCall = mApiManager.userService.logout(mUserManager.getFacebookUserId());
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

        stopService(new Intent(this, BarNotificationService.class));
        stopService(new Intent(this, BeaconService.class));

        mUserManager.logout();
        mOrderManager.removeOrder();

        LoginManager.getInstance().logOut();
        startActivity(LoginActivity.class);
        finish();
    }

    public void getCurrentOrder() {
        Call<Order> loginCall = mApiManager.userService.getCurrentOrder(mUserManager.getFacebookUserId());
        loginCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail Order");
                    return;
                }

                Order order = response.body();
                if (order != null && !order.getStep().equals(Order.Step.VALIDATE)) {
                    Log.i(TAG, "Success Order");
                    mOrderManager.order = order;
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail Order.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }
}
