package com.sao.mobile.sao.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.saolib.ui.listener.OnItemClickListener;
import com.sao.mobile.saolib.utils.UnitPriceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 23/01/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;

    private List<Product> mItems;
    private Context mContext;
    private OnItemClickListener mListener;

    private OrderManager mOrderManager = OrderManager.getInstance();

    public ProductAdapter(Context context, List<Product> items, OnItemClickListener listener) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<Product>();
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ProductViewHolder productViewHolder = (ProductViewHolder) holder;
        final Product product = mItems.get(position);

        productViewHolder.productName.setText(product.getName());
        productViewHolder.productDesc.setText(product.getDescription());
        productViewHolder.productPrice.setText(UnitPriceUtils.addEuro(product.getPrice()));
        productViewHolder.quantityFrameLayout.setVisibility(View.GONE);
        productViewHolder.productQuantity.setText(product.getQuantity());

        productViewHolder.quantityFrameLayout.setVisibility(Double.parseDouble(product.getQuantity()) > 0 ? View.VISIBLE : View.GONE);

        productViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOrderManager.addProduct(product);
                productViewHolder.quantityFrameLayout.setVisibility(View.VISIBLE);
                productViewHolder.productQuantity.setText(product.getQuantity());
                mListener.onItemClick(product);
            }
        });
    }

    private void updateQuantityFrameLayout() {
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateProductList() {
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public TextView productName;
        public TextView productDesc;
        public TextView productPrice;
        public TextView productQuantity;
        public CardView cardView;
        public FrameLayout quantityFrameLayout;

        public ProductViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.productName);
            productDesc = (TextView) view.findViewById(R.id.productDesc);
            productPrice = (TextView) view.findViewById(R.id.productPrice);
            productQuantity = (TextView) view.findViewById(R.id.productQuantity);
            cardView = (CardView) view.findViewById(R.id.card_view);
            quantityFrameLayout = (FrameLayout) view.findViewById(R.id.quantityFrameLayout);
        }
    }
}
