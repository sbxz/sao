package com.sao.mobile.sao.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.BarProductManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.fragment.BarProductsFragment;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Product;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.listener.OnItemClickListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class BarDetailActivity extends BaseActivity implements OnItemClickListener {
    private static final String TAG = BarDetailActivity.class.getSimpleName();

    public static final String BAR_EXTRA = "barExtra";

    public static final String IMAGE_TRANSITION_NAME = "imageTransition";
    public static final String POINT_TRANSITION_NAME = "pointTransition";

    private ImageView mBarThumbnail;
    private TextView mBarPoint;
    private TabLayout mBarInfosTab;
    private ViewPager mViewPager;
    private RelativeLayout mCartButton;
    private ProgressBar mLoadProgressBar;

    private TextView mCartQuantity;
    private TextView mCartPrice;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private OrderManager mOrderManager = OrderManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();
    private BarProductManager mBarProductManager = BarProductManager.getInstance();

    private Bar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_detail);
        setStatusBarTranslucent(true);

        mLoadProgressBar = (ProgressBar) findViewById(R.id.loadProgressBar);

        mBar = (Bar) getIntent().getSerializableExtra(BAR_EXTRA);

        setupHeader();
        setupTabs();
        setupFooter();

        Map<String, List<Product>> catalog = mBarProductManager.getBarCatalog(mBar);
        if(catalog == null) {
            retrieveCatalog();
        } else {
            setupCatalog(catalog);
        }

        updateCart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_infos_menu, menu);

        MenuItem infoItem = menu.findItem(R.id.action_info);
        MenuItemCompat.setActionView(infoItem, R.layout.action_icon_info);

        View view = MenuItemCompat.getActionView(infoItem);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BarInfoActivity.class);
            }
        });

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCart();

        if(mViewPager == null || mViewPager.getAdapter() == null) {
            return;
        }

        for(int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            ((BarProductsFragment)((ViewPagerAdapter) mViewPager.getAdapter()).getItem(i)).updateCart();
        }
    }

    private void setCarButtonVisible() {
        ViewGroup.MarginLayoutParams layoutParam = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
        if(mUserManager.currentBar != null && mBar.getBarId().equals(mUserManager.currentBar.getBarId()) && mOrderManager.getProductSize() > 0) {
            mCartButton.setVisibility(View.VISIBLE);
            layoutParam.bottomMargin = (int) getResources().getDimension(R.dimen.cart_button_height);
        } else {
            mCartButton.setVisibility(View.GONE);
            layoutParam.bottomMargin = 0;
        }
    }

    private void setupFooter() {
        mCartButton = (RelativeLayout) findViewById(R.id.cartButton);
        mCartPrice = (TextView) findViewById(R.id.cartPrice);
        mCartQuantity = (TextView) findViewById(R.id.cartQuantity);

        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(OrderActivity.class);
            }
        });
    }

    private void setupHeader() {
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), IMAGE_TRANSITION_NAME);
        supportPostponeEnterTransition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(mBar.getName());

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

        mBarPoint = (TextView) findViewById(R.id.barPoint);
        ViewCompat.setTransitionName(mBarPoint, POINT_TRANSITION_NAME);
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        mCollapsingToolbarLayout.setContentScrimColor(primary);
        mCollapsingToolbarLayout.setStatusBarScrimColor(primary);
        supportStartPostponedEnterTransition();
    }

    private void setupTabs() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(99);
        mBarInfosTab = (TabLayout) findViewById(R.id.barInfosTab);
        mBarInfosTab.setupWithViewPager(mViewPager);

        mBarInfosTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void retrieveCatalog() {
        showProgressLoad();
        Call<Map<String, List<Product>>> barCall = mApiManager.barService.retrieveCatalog(mBar.getBarId());
        barCall.enqueue(new retrofit2.Callback<Map<String, List<Product>>>() {
            @Override
            public void onResponse(Call<Map<String, List<Product>>> call, Response<Map<String, List<Product>>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve catalog");
                    return;
                }

                Log.i(TAG, "success retrieve catalog");
                mBarProductManager.addBarCatalog(mBar, response.body());
                setupCatalog(response.body());
            }

            @Override
            public void onFailure(Call<Map<String, List<Product>>> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve catalog.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void setupCatalog(Map<String, List<Product>> catalog) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BarProductsFragment barProductsFragment;
        for (String mapKey : catalog.keySet()) {
            barProductsFragment = new BarProductsFragment();
            barProductsFragment.addProducts(mBar.getBarId(), catalog.get(mapKey));
            adapter.addFragment(barProductsFragment, mapKey);
        }

        mViewPager.setAdapter(adapter);
        hideProgressLoad();
    }

    private void showProgressLoad() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        mBarInfosTab.setVisibility(View.GONE);
    }

    private void hideProgressLoad() {
        mLoadProgressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mBarInfosTab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(Object object) {
        updateCart();
    }

    private void updateCart() {
        setCarButtonVisible();
        mCartQuantity.setText(mOrderManager.getTotalQuantityAsString());
        mCartPrice.setText(mOrderManager.getTotalPriceAsString());
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }
}
