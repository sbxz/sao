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

import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saolib.utils.UnitPriceUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Customer;
import com.sao.mobile.saopro.entities.Order;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.ui.adapter.OrderDetailsAdapter;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends BaseActivity {
    private static final String TAG = OrderDetailsActivity.class.getSimpleName();

    public static final String ORDER_EXTRA = "orderExtra";

    public static final String IMAGE_TRANSITION_NAME = "imageTransition";
    public static final String NAME_TRANSITION_NAME = "nameTransition";
    public static final String DATE_TRANSITION_NAME = "dateTransition";
    public static final String PRICE_TRANSITION_NAME = "priceTransition";

    private RecyclerView mRecyclerView;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private Order mOrder;

    private ApiManager mApiManager = ApiManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        mOrder = (Order) getIntent().getSerializableExtra(ORDER_EXTRA);

        Button confirmButton = (Button) findViewById(R.id.confirm);
        if(mOrder.getStep().equals(Order.Step.WAIT)) {
            confirmButton.setText(mContext.getString(R.string.finish_order));
        }
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOrder.getStep().equals(Order.Step.WAIT)) {
                    finishOrder();
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
        totalPrice.setText(UnitPriceUtils.addEuro(mOrder.getTotalPrice()));
        ViewCompat.setTransitionName(totalPrice, PRICE_TRANSITION_NAME);

        TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText(Customer.getCustomerName(mOrder.getCustomer()));
        ViewCompat.setTransitionName(userName, NAME_TRANSITION_NAME);

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(mOrder.getDate());
        ViewCompat.setTransitionName(date, DATE_TRANSITION_NAME);

        ImageView thumbnail = (ImageView) findViewById(R.id.userImage);
        Picasso.with(mContext).load(mOrder.getCustomer().getThumbnail())
                .placeholder(R.drawable.sao)
                .fit()
                .centerCrop()
                .into(thumbnail);
        ViewCompat.setTransitionName(thumbnail, IMAGE_TRANSITION_NAME);
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.order_number) + mOrder.getId());
    }

    private void finishOrder() {
        Call<Void> barServiceCall = mApiManager.barService.finishOrder();
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success finish order orders");

                mOrder.setStep(Order.Step.FINISH);

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
        Call<Void> barServiceCall = mApiManager.barService.confirmOrder();
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success retrieve orders");

                mOrder.setStep(Order.Step.WAIT);

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
        mRecyclerView.setAdapter(new OrderDetailsAdapter(mContext, mOrder.getProducts()));
    }
}
