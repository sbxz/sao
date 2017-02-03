package com.sao.mobile.sao.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.api.LoginService;
import com.sao.mobile.sao.service.api.UserService;
import com.sao.mobile.sao.ui.activity.LoginActivity;
import com.sao.mobile.sao.ui.fragment.BarsFragment;
import com.sao.mobile.sao.ui.fragment.HomeFragment;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.LocalStore;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Test commit
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private Fragment mCurrentFragment;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ImageView mUserThumbnail;
    private TextView mUserName;

    private UserManager mUserManager = UserManager.getInstance();

    private LoginService mLoginService;
    private UserService mUserService;

    @Override
    protected void initServices() {
        mLoginService = retrofit.create(LoginService.class);
        mUserService = retrofit.create(UserService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setupNavigationView();
        setupMenuListener();
        initServices();
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

        mCurrentFragment = new BarsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, mCurrentFragment);
        fragmentTransaction.commit();
        setTitle(R.string.home_name);
    }

    private void setupMenuListener() {
        View headerLayout = mNavigationView.getHeaderView(0);
        mUserName = (TextView) headerLayout.findViewById(R.id.user_name);
        mUserName.setText(mUserManager.userName);

        mUserThumbnail = (ImageView) headerLayout.findViewById(R.id.userThumbnail);
        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.menu_bar_avatar_size);
        Picasso.with(mContext).load(mUserManager.userThumbnail)
                .placeholder(R.drawable.sao)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mUserThumbnail);
        mUserThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            mCurrentFragment = new HomeFragment();
        } else if (id == R.id.nav_bar) {
            mCurrentFragment = new BarsFragment();
            setTitle(R.string.menu_bars);
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

    private void logout() {
        // Clear preferences for next user
        LocalStore.clearPreferences(mContext);

        Call<Void> loginCall = mLoginService.logout();
        loginCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success logout");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail logout. Message= " + t.getMessage());
            }
        });

        startActivity(LoginActivity.class);
        finish();
    }
}
