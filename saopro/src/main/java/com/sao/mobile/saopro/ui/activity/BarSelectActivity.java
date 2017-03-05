package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.pageTransformer.CustPagerTransformer;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;
import com.sao.mobile.saopro.ui.fragment.BarSelectFragment;

import java.util.ArrayList;
import java.util.List;

public class BarSelectActivity extends BaseActivity {
    private static final String TAG = BarSelectActivity.class.getSimpleName();

    private TextView mIndicatorTextView;
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLaout;

    private List<BarSelectFragment> fragments = new ArrayList<>();

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_select);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLinearLaout = (LinearLayout) findViewById(R.id.linearLayout);
        mIndicatorTextView = (TextView) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        fillViewPager(mTraderManager.trader.getBars());
    }

    private void fillViewPager(final List<Bar> bars) {
        mViewPager.setPageTransformer(false, new CustPagerTransformer(this));

        for (Bar bar : bars) {
            fragments.add(new BarSelectFragment());
        }

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                BarSelectFragment fragment = fragments.get(position);
                fragment.bindData(bars.get(position));
                return fragment;
            }

            @Override
            public int getCount() {
                return bars.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicator();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateIndicator();
    }

    private void updateIndicator() {
        int totalNum = mViewPager.getAdapter().getCount();
        int currentItem = mViewPager.getCurrentItem() + 1;
        mIndicatorTextView.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + totalNum));
    }

    private void showProgressLoad() {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
        mIndicatorTextView.setVisibility(View.INVISIBLE);
        mLinearLaout.setVisibility(View.INVISIBLE);
    }

    private void hideProgressLoad() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mIndicatorTextView.setVisibility(View.VISIBLE);
        mLinearLaout.setVisibility(View.VISIBLE);
    }
}
