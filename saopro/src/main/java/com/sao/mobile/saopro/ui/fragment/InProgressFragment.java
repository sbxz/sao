package com.sao.mobile.saopro.ui.fragment;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.TraderOrder;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.ui.adapter.OrderAdapter;

import java.util.List;

public class InProgressFragment extends BaseFragment {
    private static final String TAG = InProgressFragment.class.getSimpleName();

    private View mView;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mNotNewOrder;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private OrderAdapter mOrderAdapter;

    public InProgressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order_new_list, container, false);

        mProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);
        mNotNewOrder = (TextView) mView.findViewById(R.id.notNewOrder);

        initRecyclerView();

        return mView;
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.orderRecylerVIew);
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

        mOrderAdapter = new OrderAdapter(mContext, null);
        mRecyclerView.setAdapter(mOrderAdapter);
    }

    public void addListOrder(List<TraderOrder> orders) {
        if(mOrderAdapter == null) {
            return;
        }
        mOrderAdapter.addListItem(orders);
        setNotNewOrderVisible();
    }

    public void addOrder(TraderOrder order) {
        mOrderAdapter.addItem(order);
        setNotNewOrderVisible();
    }

    public void removeOrder(TraderOrder order) {
        mOrderAdapter.removeOrder(order);
        setNotNewOrderVisible();
    }

    private void setNotNewOrderVisible() {
        mNotNewOrder.setVisibility(mOrderAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
    }

    public void showProgressLoad() {
        if(mProgressBar == null || mRecyclerView == null) {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressLoad() {
        if(mProgressBar == null || mRecyclerView == null) {
            return;
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
