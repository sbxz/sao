package com.sao.mobile.sao.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.ui.adapter.OrderAdapter;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

public class OrderActivity extends BaseActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();

    private OrderManager mOrderManager = OrderManager.getInstance();

    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private TextView mCatalogButton;
    private RecyclerView mOrderRecyclerView;
    private TextView mTotalPrice;
    private Button mConfirm;

    @Override
    protected void initServices() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setupHeader();
        setupInfo();
        initRecyclerView();
        
        mCatalogButton = (TextView) findViewById(R.id.catalogButton);
        mCatalogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mConfirm = (Button) findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.order_name));
    }

    private void setupInfo() {
        mTotalPrice = (TextView) findViewById(R.id.totalPrice);
        mTotalPrice.setText(mOrderManager.getTotalPriceAsString());
    }

    private void initRecyclerView() {
        mOrderRecyclerView = (RecyclerView) findViewById(R.id.orderRecylerView);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(this));
        mOrderRecyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mOrderRecyclerView.addOnScrollListener(mEndlessRecyclerScrollListener);
        mOrderRecyclerView.setAdapter(new OrderAdapter(mContext, mOrderManager.order.getProducts()));
    }


}
