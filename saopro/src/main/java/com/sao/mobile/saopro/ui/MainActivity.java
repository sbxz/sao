package com.sao.mobile.saopro.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Bar;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.UserManager;
import com.sao.mobile.saopro.ui.activity.LoginActivity;
import com.sao.mobile.saopro.ui.activity.NotificationActivity;
import com.sao.mobile.saopro.ui.fragment.BeaconFragment;
import com.sao.mobile.saopro.ui.fragment.OrderListFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private Fragment mCurrentFragment;

    private NavigationView mNavigationView;
    private ImageView mBarSelector;
    private ImageView mBarThumbnail;
    private TextView mBarName;

    private Boolean isBarListVisile = false;

    private ApiManager mApiManager = ApiManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    private Target mTarget;

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
             //   LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(OrderListFragment.NEW_ORDER_EXTRA));
                startActivity(NotificationActivity.class);
            }
        });

        setupNavigationView();
        setupMenuListener();
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
        for (Bar bar : mUserManager.bars) {
            menu.add(R.id.bar_menu, Integer.valueOf(bar.getId()), 0, bar.getName()).setIcon(R.drawable.ic_play_dark);
        }

        mCurrentFragment = new OrderListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mCurrentFragment);
        fragmentTransaction.commit();
        setTitle(mUserManager.currentBar.getName());
    }

    private void setupMenuListener() {
        View headerLayout = mNavigationView.getHeaderView(0);
        mBarName = (TextView) headerLayout.findViewById(R.id.bar_name);
        mBarName.setText(mUserManager.currentBar.getName());

        mBarThumbnail = (ImageView) headerLayout.findViewById(R.id.barThumbnail);
        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.menu_bar_avatar_size);
        Picasso.with(mContext).load(mUserManager.currentBar.getThumbnail())
                .placeholder(R.drawable.sao)
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
        if(!isBarListVisile) {
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

        isBarListVisile = !isBarListVisile;
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

        if(isBarListVisile) {
            selectNewCurrentBar(id);
            return true;
        }

        if(mCurrentFragment instanceof OrderListFragment) {
            ((OrderListFragment) mCurrentFragment).removeFragment();
        }

        if (id == R.id.nav_home) {
            mCurrentFragment = new OrderListFragment();
            setTitle(mUserManager.currentBar.getName());
        } else if (id == R.id.nav_beacon) {
            mCurrentFragment = new BeaconFragment();
            setTitle(R.string.menu_beacon);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        if(mCurrentFragment == null) {
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
        mUserManager.currentBar = mUserManager.bars.get(id - 1);
        changeMenuView();

        View headerLayout = mNavigationView.getHeaderView(0);
        mBarName = (TextView) headerLayout.findViewById(R.id.bar_name);
        mBarName.setText(mUserManager.currentBar.getName());

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.menu_bar_avatar_size);
        Picasso.with(mContext).load(mUserManager.currentBar.getThumbnail())
                .placeholder(R.drawable.sao)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mBarThumbnail);

        if(mCurrentFragment instanceof OrderListFragment) {
            ((OrderListFragment) mCurrentFragment).removeFragment();

            mCurrentFragment = new OrderListFragment();
            setTitle(mUserManager.currentBar.getName());

            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, mCurrentFragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mCurrentFragment instanceof OrderListFragment) {
            if(resultCode == Activity.RESULT_OK) {
               ((OrderListFragment) mCurrentFragment).updateOrderList(data);
            }
        } else if(mCurrentFragment instanceof BeaconFragment) {
            if(resultCode == Activity.RESULT_OK) {
                ((BeaconFragment) mCurrentFragment).updateBeaconList(data);
            }
        }
    }

    private void logout() {
        // Clear preferences for next user
        LocalStore.clearPreferences(mContext);

        Call<Void> loginCall = mApiManager.loginService.logout();
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

        startActivity(LoginActivity.class);
        finish();
    }
}
