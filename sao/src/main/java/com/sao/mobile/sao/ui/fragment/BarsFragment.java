package com.sao.mobile.sao.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.Catalog;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.service.api.UserService;
import com.sao.mobile.sao.ui.adapter.BarsAdapter;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarsFragment extends BaseFragment {
    private static final String TAG = BarsFragment.class.getSimpleName();

    private RecyclerView mBarRecyler;
    private BarsAdapter mBarAdapter;
    private ProgressBar mLoadProgressBar;
    private View mView;

    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private UserService mUserService;

    public BarsFragment() {
    }

    @Override
    protected void initServices() {
        mUserService = retrofit.create(UserService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bars, container, false);
        mLoadProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);
        
        initRecyclerView();
        refreshBarsList();

        return mView;
    }

    private void initRecyclerView() {
        mBarRecyler = (RecyclerView) mView.findViewById(R.id.barRecycler);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mBarRecyler.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mBarRecyler.addOnScrollListener(mEndlessRecyclerScrollListener);
        mBarAdapter = new BarsAdapter(mContext, null);
        mBarRecyler.setAdapter(mBarAdapter);
    }

    private void refreshBarsList() {
        Call<List<Bar>> barsCall = mUserService.retrieveBars();
        barsCall.enqueue(new Callback<List<Bar>>() {
            @Override
            public void onResponse(Call<List<Bar>> call, Response<List<Bar>> response) {
                Log.i(TAG, "Success retrieve bars");
                hideProgressLoad();
                mBarAdapter.addListItem(response.body());
            }

            @Override
            public void onFailure(Call<List<Bar>> call, Throwable t) {
                hideProgressLoad();
                Log.e(TAG, "Fail retrieve user bar. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void hideProgressLoad() {
        mLoadProgressBar.setVisibility(View.GONE);
        mBarRecyler.setVisibility(View.VISIBLE);
    }

    private void showProgressLoad() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        mBarRecyler.setVisibility(View.GONE);
    }
}
