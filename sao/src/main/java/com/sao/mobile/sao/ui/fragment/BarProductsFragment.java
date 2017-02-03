package com.sao.mobile.sao.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.ui.adapter.ProductAdapter;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.listener.OnItemClickListener;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

import java.util.List;

public class BarProductsFragment extends BaseFragment {

    private View mView;

    private RecyclerView mProductRecycler;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private List<Product> mProducts;

    @Override
    protected void initServices() {
    }

    public BarProductsFragment() {
    }

    public void addProducts(List<Product> products) {
        this.mProducts = products;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bar_products, container, false);

        setupRecylerView();

        return mView;
    }

    private void setupRecylerView() {
        mProductRecycler = (RecyclerView) mView.findViewById(R.id.productRecycler);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mProductRecycler.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mProductRecycler.addOnScrollListener(mEndlessRecyclerScrollListener);

        mProductRecycler.setAdapter(new ProductAdapter(mContext, mProducts, new OnItemClickListener() {
            @Override
            public void onItemClick(Object object) {
                ((OnItemClickListener) mContext).onItemClick(object);
            }
        }));
    }
}
