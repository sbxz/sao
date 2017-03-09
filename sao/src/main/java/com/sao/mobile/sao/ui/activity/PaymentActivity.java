package com.sao.mobile.sao.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.CardPayment;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.adapter.CardPaymentAdapter;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

import java.util.List;

public class PaymentActivity extends BaseActivity {
    private static final String TAG = PaymentActivity.class.getSimpleName();

    private TextView mNoCard;
    private FloatingActionButton mFabAdd;
    private RecyclerView mCardRecyclerView;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private CardPaymentAdapter mCardAdapter;

    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setupHeader();
        initRecyclerView();
        setupFooter();
        setupBody();

        updateCards();
    }

    private void setupBody() {
        mNoCard = (TextView) findViewById(R.id.noCard);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCardAdapter != null) {
            updateCards();
        }
    }

    private void updateCards() {
        List<CardPayment> cardPayments = mUserManager.getCardPayments(mContext);

        if (cardPayments == null || cardPayments.size() == 0) {
            return;
        }

        mCardAdapter.addListItem(cardPayments);
        showItem();
    }

    private void showNoItem() {
        mCardRecyclerView.setVisibility(View.GONE);
        mNoCard.setVisibility(View.VISIBLE);
    }

    private void showItem() {
        mCardRecyclerView.setVisibility(View.VISIBLE);
        mNoCard.setVisibility(View.GONE);
    }

    private void setupFooter() {
        mFabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AddPaymentActivity.class);
            }
        });
    }

    private void setupHeader() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle("Moyen de paiement");
    }

    private void initRecyclerView() {
        mCardRecyclerView = (RecyclerView) findViewById(R.id.cardRecylerVIew);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(this));
        mCardRecyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mCardAdapter = new CardPaymentAdapter(mContext, null);
        mCardRecyclerView.addOnScrollListener(mEndlessRecyclerScrollListener);
        mCardRecyclerView.setAdapter(mCardAdapter);
    }
}
