package com.sao.mobile.sao.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.MainActivity;
import com.sao.mobile.sao.ui.activity.BarActivity;
import com.sao.mobile.sao.ui.adapter.HomeAdapter;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends BaseFragment {
    public static final String UPDATE_CURRENT_BAR = "updateCurrentBar";
    private static final String TAG = HomeFragment.class.getSimpleName();
    private View mView;

    private FrameLayout mCurrentBarLayout;
    private ImageView mBarThumbnail;
    private TextView mBarName;
    private TextView mOrderStatus;

    private Button mTopBarSeeMore;
    private ImageView mBarTopThumb1;
    private ImageView mBarTopThumb2;
    private ImageView mBarTopThumb3;
    private TextView mBarTopName1;
    private TextView mBarTopName2;
    private TextView mBarTopName3;

    private NestedScrollView mMainScrollView;
    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ProgressBar mLoadProgressBar;

    private int currentNewsPage = 0;

    private OrderManager mOrderManager = OrderManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mLoadProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);

        setupCurrentBar();
        setupRecyclerView();

        refreshData();

        return mView;
    }

    public void onResume() {
        super.onResume();
        setupCurrentBar();
        // refreshData();
    }

    private void refreshData() {
        showProgressLoad();

        Call<List<News>> newsCall = mApiManager.barService.getNews(mUserManager.getFacebookUserId(), currentNewsPage, MainActivity.DEFAULT_SIZE_PAGE);
        newsCall.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve news");
                    return;
                }

                Log.i(TAG, "Success retrieve news");
                if (response.body().size() > 0) {
                    mHomeAdapter.addListItem(response.body());
                    currentNewsPage++;
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve news.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });

        Call<List<Bar>> topBarCall = mApiManager.barService.getTopBar();
        topBarCall.enqueue(new Callback<List<Bar>>() {
            @Override
            public void onResponse(Call<List<Bar>> call, Response<List<Bar>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve top bar");
                    return;
                }

                Log.i(TAG, "Fail retrieve top bar");
                updateTopBar(response.body());
            }

            @Override
            public void onFailure(Call<List<Bar>> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve to bar.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void updateTopBar(List<Bar> bars) {
        final Bar bar1 = bars.get(0);
        final Bar bar2 = bars.get(1);
        final Bar bar3 = bars.get(2);

        mTopBarSeeMore = (Button) mView.findViewById(R.id.topBarSeeMore);
        mBarTopThumb1 = (ImageView) mView.findViewById(R.id.barTopThumb1);
        mBarTopThumb2 = (ImageView) mView.findViewById(R.id.barTopThumb2);
        mBarTopThumb3 = (ImageView) mView.findViewById(R.id.barTopThumb3);
        mBarTopName1 = (TextView) mView.findViewById(R.id.barTopName1);
        mBarTopName2 = (TextView) mView.findViewById(R.id.barTopName2);
        mBarTopName3 = (TextView) mView.findViewById(R.id.barTopName3);

        mTopBarSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mBarTopThumb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBar(mBarTopThumb1, bar1);
            }
        });
        mBarTopThumb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBar(mBarTopThumb2, bar2);
            }
        });
        mBarTopThumb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBar(mBarTopThumb3, bar3);
            }
        });

        Picasso.with(mContext).load(bar1.getThumbnail())
                .fit()
                .centerCrop()
                .into(mBarTopThumb1);
        Picasso.with(mContext).load(bar2.getThumbnail())
                .fit()
                .centerCrop()
                .into(mBarTopThumb2);
        Picasso.with(mContext).load(bar3.getThumbnail())
                .fit()
                .centerCrop()
                .into(mBarTopThumb3);

        mBarTopName1.setText(bar1.getName());
        mBarTopName2.setText(bar2.getName());
        mBarTopName3.setText(bar3.getName());
    }

    private void showProgressLoad() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideProgressLoad() {
        mLoadProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void setupCurrentBar() {
        mMainScrollView = (NestedScrollView) mView.findViewById(R.id.mainScrollView);
        mCurrentBarLayout = (FrameLayout) mView.findViewById(R.id.currentBarLayout);
        mBarThumbnail = (ImageView) mView.findViewById(R.id.barThumbnail);
        mBarName = (TextView) mView.findViewById(R.id.barName);
        mOrderStatus = (TextView) mView.findViewById(R.id.orderStatus);

        mCurrentBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCurrentBar();
            }
        });

        if (mUserManager.currentBar == null) {
            mCurrentBarLayout.setVisibility(View.GONE);
            return;
        }

        mCurrentBarLayout.setVisibility(View.VISIBLE);
        mBarName.setText(mUserManager.currentBar.getName());

        String orderStatus = "";

        if (mOrderManager.isProductOk() && mOrderManager.order.getStep().equals(Order.Step.INPROGRESS)) {
            orderStatus = getString(R.string.order_step_in_progress);
        } else if (mOrderManager.isProductOk() && mOrderManager.order.getStep().equals(Order.Step.READY)) {
            orderStatus = getString(R.string.order_step_ready);
        } else {
            orderStatus = getString(R.string.order_step_validate);
        }

        mOrderStatus.setText(orderStatus);

        Picasso.with(mContext).load(mUserManager.currentBar.getThumbnail())
                .fit()
                .centerCrop()
                .into(mBarThumbnail);
    }

    private void goToBar(ImageView imageView, Bar bar) {
        Activity activity = (Activity) mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(imageView, BarActivity.IMAGE_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, BarActivity.class);
        intent.putExtra(BarActivity.BAR_EXTRA, bar);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void goToCurrentBar() {
        Activity activity = (Activity) mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(mBarThumbnail, BarActivity.IMAGE_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, BarActivity.class);
        intent.putExtra(BarActivity.BAR_EXTRA, mUserManager.currentBar);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void addNews(News news) {
        if (mHomeAdapter == null) {
            return;
        }

        mHomeAdapter.addItem(news);
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.homeRecycerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        final PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mRecyclerView.setLayoutManager(layoutManager);

        mMainScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    loadMoreNews();
                }
            }
        });

        mHomeAdapter = new HomeAdapter(mContext, null);
        mRecyclerView.setAdapter(mHomeAdapter);
    }

    private void loadMoreNews() {
        if (!mHomeAdapter.isMoreDataAvailable) {
            return;
        }

        mHomeAdapter.addLoadItem();
        Call<List<News>> newsCall = mApiManager.barService.getNews(mUserManager.getFacebookUserId(), currentNewsPage, MainActivity.DEFAULT_SIZE_PAGE);
        newsCall.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                mHomeAdapter.removeLoadItem();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "Fail retrieve news");
                    mHomeAdapter.isMoreDataAvailable = false;
                    return;
                }

                if (response.body().size() > 0) {
                    mHomeAdapter.pushNews(response.body());
                    currentNewsPage++;
                } else {
                    mHomeAdapter.isMoreDataAvailable = false;
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                mHomeAdapter.removeLoadItem();
                mHomeAdapter.isMoreDataAvailable = false;
                mHomeAdapter.notifyDataSetChanged();
                LoggerUtils.apiFail(TAG, "Fail retrieve news.", t);
            }
        });
    }
}
