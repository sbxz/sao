package com.sao.mobile.sao.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.ui.activity.BarActivity;
import com.sao.mobile.sao.ui.activity.ConsumptionActivity;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.api.MyOrder;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.sao.mobile.saolib.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ConsumptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyOrder> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public ConsumptionAdapter(Context context, List<MyOrder> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<MyOrder>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_my_order, parent, false);
        return new MyOrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyOrderViewHolder myOrderViewHolder = (MyOrderViewHolder) holder;
        final MyOrder myOrder = mItems.get(position);
        final Bar bar = myOrder.getBar();

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Picasso.with(mContext).load(myOrder.getBar().getThumbnail())
                .resize(avatarSize, avatarSize)
                .transform(new CircleTransformation())
                .centerCrop()
                .into(myOrderViewHolder.barImage);

        myOrderViewHolder.numOrder.setText("#"+myOrder.getOrder().getOrderId().toString());
        myOrderViewHolder.barName.setText(myOrder.getBar().getName());
        myOrderViewHolder.date.setText(Utils.getRelativeTime(myOrder.getOrder().getDate()));
        myOrderViewHolder.numberProduct.setText("Commande de "+myOrder.getOrder().getTotalQuantity()+" produits");
        myOrderViewHolder.price.setText(myOrder.getOrder().getTotalPrice().toString()+" Euros");

        myOrderViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMyOrderDetail(myOrderViewHolder, bar, myOrder);
            }
        });
    }


    private void goToMyOrderDetail(MyOrderViewHolder myOrderViewHolder, Bar bar, MyOrder myOrder) {
        Activity activity = (Activity) mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(myOrderViewHolder.barImage, ConsumptionActivity.IMAGE_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, ConsumptionActivity.class);
        intent.putExtra(BarActivity.BAR_EXTRA, bar);
        intent.putExtra(ConsumptionActivity.MY_ORDER_EXTRA, myOrder);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<MyOrder> myOrders) {
        mItems.addAll(myOrders);
        notifyDataSetChanged();
    }

    public void addItem(MyOrder myOrder) {
        mItems.add(myOrder);
        notifyDataSetChanged();
    }

    private static class MyOrderViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView barImage;
        TextView numOrder;
        TextView barName;
        TextView date;
        TextView numberProduct;
        TextView price;

        MyOrderViewHolder (View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            barImage = (ImageView) view.findViewById(R.id.barImage);
            numOrder = (TextView) view.findViewById(R.id.numOrder);
            barName = (TextView) view.findViewById(R.id.barName);
            date = (TextView) view.findViewById(R.id.date);
            numberProduct = (TextView) view.findViewById(R.id.numberProduct);
            price = (TextView) view.findViewById(R.id.price);
        }

    }
}
