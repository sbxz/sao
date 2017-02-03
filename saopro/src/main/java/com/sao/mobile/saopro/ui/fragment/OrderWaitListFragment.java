package com.sao.mobile.saopro.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Order;
import com.sao.mobile.saopro.service.api.BarService;
import com.sao.mobile.saopro.ui.adapter.OrderAdapter;

public class OrderWaitListFragment extends BaseFragment {
    private static final String TAG = OrderWaitListFragment.class.getSimpleName();

    private View mView;
    private ProgressBar mProgressBar;
    private TextView mNotWaitOrder;
    private RecyclerView mRecyclerView;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private BarService mBarService;

    private OrderAdapter mOrderAdapter;

    public OrderWaitListFragment() {
    }

    @Override
    protected void initServices() {
        mBarService = retrofit.create(BarService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order_wait_list, container, false);

        mProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);
        mNotWaitOrder = (TextView) mView.findViewById(R.id.notWaitOrder);

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

    public void removeOrder(Order order) {
        mOrderAdapter.removeOrder(order);
        setNotWaitOrderVisible();
    }

    public void addOrder(Order order) {
        mOrderAdapter.addItem(order);
        setNotWaitOrderVisible();
    }

    public void showProgressLoad() {
        if(mProgressBar == null || mRecyclerView == null) {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        setNotWaitOrderVisible();
    }

    private void setNotWaitOrderVisible() {
        mNotWaitOrder.setVisibility(mOrderAdapter.getItemCount() <= 0 ? View.VISIBLE : View.GONE);
    }

    public void hideProgressLoad() {
        if(mProgressBar == null || mRecyclerView == null) {
            return;
        }

        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        setNotWaitOrderVisible();
    }
}
