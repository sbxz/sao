package com.sao.mobile.saopro.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sao.mobile.saolib.utils.UnitPriceUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 22/12/2016.
 */

public class OrderDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public OrderDetailsAdapter(Context context, List<Product> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<Product>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_order_details, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProductViewHolder orderHolder = (ProductViewHolder) holder;
        final Product product = mItems.get(position);
        orderHolder.productName.setText(product.getName());
        orderHolder.productPrice.setText(UnitPriceUtils.addEuro(product.getPrice()));
        orderHolder.productQuantity.setText(product.getQuantity());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public TextView productQuantity;
        public TextView productName;
        public TextView productPrice;

        public ProductViewHolder(View view) {
            super(view);

            productQuantity = (TextView) view.findViewById(R.id.productQuantity);
            productName = (TextView) view.findViewById(R.id.productName);
            productPrice = (TextView) view.findViewById(R.id.productPrice);
        }
    }
}
