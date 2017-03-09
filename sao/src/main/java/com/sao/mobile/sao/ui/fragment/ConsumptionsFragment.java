package com.sao.mobile.sao.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.adapter.BarsAdapter;
import com.sao.mobile.sao.ui.adapter.ConsumptionAdapter;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.api.MyOrder;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConsumptionsFragment extends BaseFragment {
    private static final String TAG = ConsumptionsFragment.class.getSimpleName();

    private RecyclerView mConsumptionRecyler;
    private ConsumptionAdapter mConsumptionAdapter;
    private ProgressBar mLoadProgressBar;
    private View mView;

    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private UserManager mUserManager = UserManager.getInstance();
    private ApiManager mApiManager = ApiManager.getInstance();

    public ConsumptionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_consumptions, container, false);
        mLoadProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);

        initRecyclerView();
        refreshConsumptionsList();

        return mView;
    }

    private void initRecyclerView() {
        mConsumptionRecyler = (RecyclerView) mView.findViewById(R.id.consumptionRecycler);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mConsumptionRecyler.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mConsumptionRecyler.addOnScrollListener(mEndlessRecyclerScrollListener);
        mConsumptionAdapter = new ConsumptionAdapter(mContext, null);
        mConsumptionRecyler.setAdapter(mConsumptionAdapter);
    }

    private void refreshConsumptionsList() {
        showProgressLoad();
        Call<List<MyOrder>> barsCall = mApiManager.userService.retrieveFinishOrder(mUserManager.getFacebookUserId());
        barsCall.enqueue(new Callback<List<MyOrder>>() {
            @Override
            public void onResponse(Call<List<MyOrder>> call, Response<List<MyOrder>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve bars");
                    return;
                }

                Log.i(TAG, "Success retrieve bars");
                mConsumptionAdapter.addListItem(response.body());
            }

            @Override
            public void onFailure(Call<List<MyOrder>> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve user bar.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void hideProgressLoad() {
        mLoadProgressBar.setVisibility(View.GONE);
        mConsumptionRecyler.setVisibility(View.VISIBLE);
    }

    private void showProgressLoad() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        mConsumptionRecyler.setVisibility(View.GONE);
    }
}

