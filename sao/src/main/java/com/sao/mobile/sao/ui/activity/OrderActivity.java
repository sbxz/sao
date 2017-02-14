package com.sao.mobile.sao.ui.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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

import com.estimote.sdk.cloud.internal.User;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Order;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.service.BarNotificationService;
import com.sao.mobile.sao.service.api.BarService;
import com.sao.mobile.sao.ui.adapter.OrderAdapter;
import com.sao.mobile.sao.ui.fragment.BarProductsFragment;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

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

    private BarService mBarService;

    @Override
    protected void initServices() {
        mBarService = retrofit.create(BarService.class);
    }

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
                confirmOrder();
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
        if(mOrderManager.order.getStep().equals(Order.Step.START)) {
            mConfirm.setVisibility(View.VISIBLE);
            mStepOrderCard.setVisibility(View.GONE);
        } else if(mOrderManager.order.getStep().equals(Order.Step.WAIT)){
            mConfirm.setVisibility(View.GONE);
            mStepOrderCard.setVisibility(View.VISIBLE);
            mOrderStepText.setText(getString(R.string.order_step_wait));
        }  else if(mOrderManager.order.getStep().equals(Order.Step.FINISH)){
            mConfirm.setVisibility(View.GONE);
            mStepOrderCard.setVisibility(View.VISIBLE);
            mOrderStepText.setText(getString(R.string.order_step_finish));
        }
    }

    private void confirmOrder() {
        if(mUserManager.currentBar == null) {
            Snackbar.make(getView(), R.string.btn_no_bar, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        Call<Void> barCall = mBarService.launchTraderOrder();
        barCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success confirm order");
                Snackbar.make(getView(), getString(R.string.order_step_wait), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mOrderManager.setWaitOrder();
                updateViewByStep();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(
                        BarNotificationService.BAR_NOTIFICATION_ID,
                        BarNotificationService.getBarNotification(mContext, mUserManager.currentBar, mUserManager.currentBar.getBarName(), getString(R.string.order_step_wait), mUserManager.currentBar.getBarThumbnail()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fail confirm order. Message= " + t.getMessage());
                Snackbar.make(getView(), "Fail", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
