package com.sao.mobile.saopro.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.TraderOrder;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saolib.utils.UnitPriceUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.ui.adapter.OrderDetailsAdapter;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends BaseActivity {
    public static final String ORDER_EXTRA = "orderExtra";
    public static final String IMAGE_TRANSITION_NAME = "imageTransition";
    public static final String NAME_TRANSITION_NAME = "nameTransition";
    public static final String DATE_TRANSITION_NAME = "dateTransition";
    public static final String PRICE_TRANSITION_NAME = "priceTransition";
    private static final String TAG = OrderDetailsActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private TraderOrder mOrder;

    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        mOrder = (TraderOrder) getIntent().getSerializableExtra(ORDER_EXTRA);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        if(mOrder.getStep().equals(Order.Step.READY)) {
            confirmButton.setText(mContext.getString(R.string.finish_order));
        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOrder.getStep().equals(Order.Step.READY)) {
                    validateOrder();
                } else {
                    confirmOrder();
                }
            }
        });

        setupHeader();
        setupInfo();
        initRecyclerView();
    }

    private void setupInfo() {
        TextView totalPrice = (TextView) findViewById(R.id.totalPrice);
        totalPrice.setText(UnitPriceUtils.addEuro(String.valueOf(mOrder.getTotalPrice())));
        ViewCompat.setTransitionName(totalPrice, PRICE_TRANSITION_NAME);

        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(mOrder.getUser().getName());
        ViewCompat.setTransitionName(userName, NAME_TRANSITION_NAME);

        TextView date = (TextView) findViewById(R.id.date);
        //date.setText(new Date(mOrder.getDate()).toString());
        ViewCompat.setTransitionName(date, DATE_TRANSITION_NAME);

        ImageView thumbnail = (ImageView) findViewById(R.id.userImage);
        Picasso.with(mContext).load(mOrder.getUser().getThumbnail())
                .fit()
                .centerCrop()
                .into(thumbnail);
        ViewCompat.setTransitionName(thumbnail, IMAGE_TRANSITION_NAME);
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.order_number) + mOrder.getOrderId());
    }

    private void validateOrder() {
        Call<Void> barServiceCall = mApiManager.barService.validateOrder(mOrder.getOrderId());
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail validate order");
                    return;
                }

                Log.i(TAG, "Success validate order orders");

                mOrder.setStep(Order.Step.VALIDATE);

                Intent intent = new Intent();
                intent.putExtra(ORDER_EXTRA, mOrder);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail finish orders.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void confirmOrder() {
        Call<Void> barServiceCall = mApiManager.barService.confirmOrder(mOrder.getOrderId());
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    Log.i(TAG, "Fail confirm order");
                    return;
                }

                Log.i(TAG, "Success confirm orders");
                mOrder.setStep(Order.Step.READY);

                Intent intent = new Intent();
                intent.putExtra(ORDER_EXTRA, mOrder);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                LoggerUtils.apiFail(TAG, "Fail confirm order.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.orderRecylerView);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(this));
        mRecyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerScrollListener);
        mRecyclerView.setAdapter(new OrderDetailsAdapter(mContext, mOrder.getOrderProducts()));
    }
}
