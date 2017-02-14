package com.sao.mobile.sao.ui.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.sdk.cloud.internal.User;
import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Order;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.activity.BarDetailActivity;
import com.sao.mobile.sao.ui.fragment.BarProductsFragment;
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
    private String mBarId;
    private Context mContext;
    private OnItemClickListener mListener;

    private OrderManager mOrderManager = OrderManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    public ProductAdapter(Context context, String barId, List<Product> items, OnItemClickListener listener) {
        this.mBarId = barId;
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<Product>();
        this.mListener = listener;
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
                if(!isOrderOk()) {
                    return;
                }

                Product productOrder = mOrderManager.addProduct(product);
                if (productOrder == null) {
                    return;
                }

                productViewHolder.quantityFrameLayout.setVisibility(View.VISIBLE);
                productViewHolder.productQuantity.setText(product.getQuantity());
                mListener.onItemClick(product);
            }
        });
    }

    private boolean isOrderOk() {
        if (mUserManager.currentBar == null || !mBarId.equals(mUserManager.currentBar.getId())) {
            return false;
        }

        if(mOrderManager.order != null && mOrderManager.order.getStep().equals(Order.Step.WAIT)) {
            Snackbar.make(((BarDetailActivity) mContext).getView(), R.string.order_step_wait, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        } else if(mOrderManager.order != null && mOrderManager.order.getStep().equals(Order.Step.FINISH)) {
            Snackbar.make(((BarDetailActivity) mContext).getView(), R.string.order_step_finish, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateProductList() {
        if (mUserManager.currentBar == null || !mBarId.equals(mUserManager.currentBar.getId())) {
            return;
        }

        Boolean isFind = false;
        for (Product product : mItems) {
            isFind = false;
            if (mOrderManager.order != null && mOrderManager.order.getProducts() != null) {
                for (Product orderProduct : mOrderManager.order.getProducts()) {
                    if (product.getId().equals(orderProduct.getId())) {
                        isFind = true;
                        product.setQuantity(orderProduct.getQuantity());
                    }
                }
            }

            if (!isFind) {
                product.setQuantity("0");
            }

        }
        notifyDataSetChanged();
    }

    private static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productDesc;
        TextView productPrice;
        TextView productQuantity;
        CardView cardView;
        FrameLayout quantityFrameLayout;

        ProductViewHolder(View view) {
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
