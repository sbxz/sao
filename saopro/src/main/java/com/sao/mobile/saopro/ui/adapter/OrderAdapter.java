package com.sao.mobile.saopro.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import com.sao.mobile.saolib.utils.UnitPriceUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Customer;
import com.sao.mobile.saopro.entities.Order;
import com.sao.mobile.saopro.ui.activity.OrderDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 01/12/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int REQUEST_CODE = 1;

    private List<Order> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public OrderAdapter(Context context, List<Order> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<Order>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final OrderViewHolder orderHolder = (OrderViewHolder) holder;
        final Order order = mItems.get(position);


        orderHolder.date.setText(order.getDate());
        orderHolder.price.setText(UnitPriceUtils.addEuro(order.getTotalPrice()));
        orderHolder.userName.setText(Customer.getCustomerName(order.getCustomer()));
        orderHolder.orderId.setText(mContext.getString(R.string.order_number) + order.getId());

        Picasso.with(mContext).load(order.getCustomer().getThumbnail())
                .placeholder(R.drawable.sao)
                .fit()
                .centerCrop()
                .into(orderHolder.userImage);

        orderHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrderDetail(orderHolder, order);
            }
        });

        if(order.getStep().equals(Order.Step.WAIT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                orderHolder.orderId.setTextColor(mContext.getColor(R.color.teal_dark));
            }
            orderHolder.orderId.setText(mContext.getString(R.string.order_number) + order.getId() + " " + mContext.getString(R.string.wait_customer));
        }
    }

    private void goToOrderDetail(OrderViewHolder orderHolder, Order order) {
        Activity activity = (Activity) mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(orderHolder.userImage, OrderDetailsActivity.IMAGE_TRANSITION_NAME),
                new Pair(orderHolder.userName, OrderDetailsActivity.NAME_TRANSITION_NAME),
                new Pair(orderHolder.date, OrderDetailsActivity.DATE_TRANSITION_NAME),
                new Pair(orderHolder.price, OrderDetailsActivity.PRICE_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, OrderDetailsActivity.class);
        intent.putExtra(OrderDetailsActivity.ORDER_EXTRA, order);
        ActivityCompat.startActivityForResult(activity, intent, REQUEST_CODE, options.toBundle());
    }

    public void updateOrderWaitList(Order order) {
        for (Order ord : mItems) {
            if(ord.getId().equals(order.getId())) {
                int index = mItems.indexOf(ord);
                mItems.remove(index);
                mItems.add(0, order);


                notifyItemMoved(index, 0);
                notifyItemChanged(0);

                return;
            }
        }
    }

    public void removeOrder(Order order) {
        for (Order ord : mItems) {
            if(ord.getId().equals(order.getId())) {
                int index = mItems.indexOf(ord);
                mItems.remove(index);
                notifyItemRemoved(index);

                return;
            }
        }
    }

    public void addListItem(List<Order> order) {
        mItems.addAll(order);
        notifyDataSetChanged();
    }

    public void addItem(Order order) {
        mItems.add(order);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;

        public ImageView userImage;
        public TextView orderId;
        public TextView userName;
        public TextView date;
        public TextView numberProduct;
        public TextView price;

        public OrderViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            userImage = (ImageView) view.findViewById(R.id.userImage);
            orderId = (TextView) view.findViewById(R.id.orderId);
            userName = (TextView) view.findViewById(R.id.userName);
            date = (TextView) view.findViewById(R.id.date);
            numberProduct = (TextView) view.findViewById(R.id.numberProduct);
            price = (TextView) view.findViewById(R.id.price);
        }
    }
}