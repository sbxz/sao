package com.sao.mobile.sao.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.entities.OrderProduct;

import java.util.ArrayList;
import java.util.List;

public class ConsumptionProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OrderProduct> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public ConsumptionProductAdapter(Context context, List<OrderProduct> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<OrderProduct>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_my_order_product, parent, false);
        return new MyOrderProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyOrderProductViewHolder viewHolder = (MyOrderProductViewHolder) holder;
        final OrderProduct orderProduct = mItems.get(position);

        viewHolder.productName.setText(orderProduct.getProduct().getName());
        viewHolder.productQuantity.setText("x"+String.valueOf(orderProduct.getQuantity()));
        viewHolder.productPrice.setText(String.valueOf(orderProduct.getPrice())+" Euros");

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<OrderProduct> myOrders) {
        mItems.addAll(myOrders);
        notifyDataSetChanged();
    }

    public void addItem(OrderProduct myOrder) {
        mItems.add(myOrder);
        notifyDataSetChanged();
    }

    private static class MyOrderProductViewHolder extends RecyclerView.ViewHolder{
        TextView productQuantity;
        TextView productPrice;
        TextView productName;

        MyOrderProductViewHolder (View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.productName);
            productPrice = (TextView) view.findViewById(R.id.productPrice);
            productQuantity = (TextView) view.findViewById(R.id.productQuantity);
        }

    }
}
