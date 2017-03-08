package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.adapter.NewsAdapter;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NewsActivity extends BaseActivity {
    private static final String TAG = NewsActivity.class.getSimpleName();

    private Bar mBar;

    private RecyclerView mRecyclerView;
    private NewsAdapter mNewsAdapter;
    private ProgressBar mLoadProgressBar;

    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        setupHeader();

        mLoadProgressBar = (ProgressBar) findViewById(R.id.loadProgressBar);
        mBar = (Bar) getIntent().getSerializableExtra(BarActivity.BAR_EXTRA);

        setupHeader();
        setupRecyclerView();

        retrieveData();
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.bar_news_title));
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.newsRecycler);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(this));
        mRecyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerScrollListener);
        mNewsAdapter = new NewsAdapter(mContext, null);
        mRecyclerView.setAdapter(mNewsAdapter);
    }

    private void retrieveData() {
        showProgressLoad();
        Call<List<News>> barCall = mApiManager.barService.retrieveNewsBar(mBar.getBarId());
        barCall.enqueue(new retrofit2.Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve news");
                    return;
                }

                Log.i(TAG, "success retrieve news");
                mNewsAdapter.addListItem(response.body());
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve news.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void showProgressLoad() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideProgressLoad() {
        mLoadProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
