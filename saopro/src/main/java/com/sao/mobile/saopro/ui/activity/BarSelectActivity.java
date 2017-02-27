package com.sao.mobile.saopro.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.pageTransformer.CustPagerTransformer;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Bar;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.ui.fragment.BarSelectFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarSelectActivity extends BaseActivity {
    private static final String TAG = BarSelectActivity.class.getSimpleName();

    private TextView mIndicatorTextView;
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLaout;

    private List<BarSelectFragment> fragments = new ArrayList<>();

    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_select);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLinearLaout = (LinearLayout) findViewById(R.id.linearLayout);
        mIndicatorTextView = (TextView) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        retrieveTraderBars();

        showProgressLoad();
    }

    private void retrieveTraderBars() {
        Call<Void> retrieveBarCall = mApiManager.barService.retrieveTraderBars();
        retrieveBarCall.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success retrieve trader bar");
                hideProgressLoad();
                List<Bar> bars = parseBarData();
                fillViewPager(bars);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Failed retrieve trader bar.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
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

    private List<Bar> parseBarData() {
        List<Bar> bars = new ArrayList<Bar>();
        bars.add(new Bar("1", "Red house", "http://i.imgur.com/CqmBjo5.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        bars.add(new Bar("2", "BreakBar", "http://i.imgur.com/zkaAooq.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        bars.add(new Bar("3", "La Kolok", "http://i.imgur.com/0gqnEaY.jpg", "56 rue de l'abondance, 69003, Lyon", "0668370384"));
        return bars;
    }
}
