package com.sao.mobile.sao.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.activity.BarActivity;
import com.sao.mobile.sao.ui.adapter.HomeAdapter;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends BaseFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    public static final String UPDATE_CURRENT_BAR = "updateCurrentBar";

    private View mView;

    private CollapsingToolbarLayout mCurrentBarLayout;
    private ImageView mBarThumbnail;
    private TextView mBarName;
    private TextView mOrderStatus;

    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ProgressBar mLoadProgressBar;

    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

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
        Call<List<News>> newsCall = mApiManager.barService.getNews(mUserManager.getFacebookUserId());
        newsCall.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve news");
                    return;
                }

                Log.i(TAG, "Success retrieve news");
                mHomeAdapter.addListItem(response.body());
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

    public void setupCurrentBar() {
        mCurrentBarLayout = (CollapsingToolbarLayout) mView.findViewById(R.id.currentBarLayout);
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

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Picasso.with(mContext).load(mUserManager.currentBar.getThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(mBarThumbnail);
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

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.homeRecycerView);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mRecyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerScrollListener);
        mHomeAdapter = new HomeAdapter(mContext, null);
        mRecyclerView.setAdapter(mHomeAdapter);
    }
}
