package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.ApiManager;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.BarNotificationService;
import com.sao.mobile.sao.ui.adapter.OrderAdapter;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends BaseActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();

    private OrderManager mOrderManager = OrderManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private RecyclerView mOrderRecyclerView;
    private TextView mTotalPrice;
    private Button mConfirm;
    private CardView mStepOrderCard;
    private TextView mOrderStepText;

    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        setupHeader();
        setupInfo();
        initRecyclerView();

        mStepOrderCard = (CardView) findViewById(R.id.stepOrderCard);
        mOrderStepText = (TextView) findViewById(R.id.orderStepText);
        mConfirm = (Button) findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrder();
            }
        });

        updateViewByStep();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViewByStep();
    }

    private void updateViewByStep() {
        if(mOrderManager.order.getStep().equals(Order.Step.NEW)) {
            mConfirm.setVisibility(View.VISIBLE);
            mStepOrderCard.setVisibility(View.GONE);
        } else if(mOrderManager.order.getStep().equals(Order.Step.INPROGRESS)){
            mConfirm.setVisibility(View.GONE);
            mStepOrderCard.setVisibility(View.VISIBLE);
            mOrderStepText.setText(getString(R.string.order_step_in_progress));
        }  else if(mOrderManager.order.getStep().equals(Order.Step.READY)){
            mConfirm.setVisibility(View.GONE);
            mStepOrderCard.setVisibility(View.VISIBLE);
            mOrderStepText.setText(getString(R.string.order_step_ready));
        }
    }

    private void startOrder() {
        if(mUserManager.currentBar == null) {
            Snackbar.make(getView(), R.string.btn_no_bar, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        Call<Order> barCall = mApiManager.barService.startOrder(mOrderManager.order);
        barCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail start order");
                    return;
                }

                Log.i(TAG, "Success start order");

                mOrderManager.order = response.body();
                updateViewByStep();

                BarNotificationService.notifyBarNotification(getBaseContext(), mUserManager.currentBar, getText(R.string.order_step_in_progress).toString());
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail confirm order.", t);
                SnackBarUtils.showSnackError(getView());
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
        updateCart();
    }

    private void updateCart() {
        mTotalPrice.setText(mOrderManager.getTotalPriceAsString());
        if(mOrderManager.getTotalQuantityAsString().equals("0")) {
            mOrderManager.removeOrder();
            finish();
        }
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
        mOrderRecyclerView.setAdapter(new OrderAdapter(mContext, mOrderManager.order.getProducts(), new OrderAdapter.OnCartUpdate() {
            @Override
            public void onUpdateOrder() {
                updateCart();
            }
        }));
    }
}
