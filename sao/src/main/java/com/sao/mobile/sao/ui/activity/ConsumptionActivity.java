package com.sao.mobile.sao.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.ui.adapter.ConsumptionAdapter;
import com.sao.mobile.sao.ui.adapter.ConsumptionProductAdapter;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Product;
import com.sao.mobile.saolib.entities.api.MyOrder;
import com.sao.mobile.saolib.ui.base.BaseActivity;
import com.sao.mobile.saolib.ui.listener.OnItemClickListener;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.sao.mobile.sao.ui.activity.BarActivity.IMAGE_TRANSITION_NAME;

public class ConsumptionActivity extends BaseActivity {
    private static final String TAG = BarInfoActivity.class.getSimpleName();

    public static final String IMAGE_TRANSITION_NAME = "imageTransition";
    public static final String BAR_EXTRA = "barExtra";
    public static final String MY_ORDER_EXTRA = "myOrderExtra";

    private ImageView mBarThumbnail;
    private TextView mNumOrder;
    private TextView mTotalPrice;

    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;
    private RecyclerView mOrderRecyclerView;

    private TextView mBarName;
    private TextView mDate;
    private TextView mNumberProduct;

    private Bar mBar;
    private MyOrder mMyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);
        setStatusBarTranslucent(true);

        mBar = (Bar) getIntent().getSerializableExtra(BAR_EXTRA);
        mMyOrder = (MyOrder) getIntent().getSerializableExtra(MY_ORDER_EXTRA);

        setupHeader();
        initRecyclerView();
        setupBody();
    }

    private void setupHeader() {
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), IMAGE_TRANSITION_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        getSupportActionBar().setTitle("");

        mBarThumbnail = (ImageView) findViewById(R.id.barThumbnail);
        Picasso.with(mContext).load(mBar.getThumbnail()).fit().centerCrop().into(mBarThumbnail, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) mBarThumbnail.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    private void setupBody() {
        mBarName = (TextView) findViewById(R.id.barName);
        mBarName.setText(mBar.getName());

        mNumOrder = (TextView) findViewById(R.id.numOrder);
        mNumOrder.setText("#" + mMyOrder.getOrder().getOrderId().toString());

        long now = System.currentTimeMillis();
        CharSequence relavetime = DateUtils.getRelativeTimeSpanString(
                mMyOrder.getOrder().getDate(),
                now,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);

        mDate = (TextView) findViewById(R.id.date);
        mDate.setText(relavetime);

        mNumberProduct = (TextView) findViewById(R.id.numberProduct);
        mNumberProduct.setText("Commande de " + mMyOrder.getOrder().getTotalQuantity() + " produits");

        mTotalPrice = (TextView) findViewById(R.id.totalPrice);
        mTotalPrice.setText(mMyOrder.getOrder().getTotalPrice().toString()+" Euros");
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
        mOrderRecyclerView.setAdapter(new ConsumptionProductAdapter(mContext, mMyOrder.getOrder().getOrderProducts()));
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(primary);
        collapsingToolbarLayout.setStatusBarScrimColor(primary);
        supportStartPostponedEnterTransition();
    }

}
