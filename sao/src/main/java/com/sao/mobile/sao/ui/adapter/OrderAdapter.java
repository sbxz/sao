package com.sao.mobile.sao.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.Product;
import com.sao.mobile.saolib.utils.UnitPriceUtils;

import java.util.ArrayList;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> mItems;
    private Context mContext;
    private OrderAdapter.OnCartUpdate mListener;

    private LayoutInflater mLayoutInflater;

    private OrderManager mOrderManager = OrderManager.getInstance();

    public OrderAdapter(Context context, List<Product> items, OrderAdapter.OnCartUpdate listener) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<Product>();
        this.mListener = listener;
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
        orderHolder.productPrice.setText(UnitPriceUtils.addEuro(String.valueOf(Double.parseDouble(product.getPrice().toString()) * product.getQuantity())));
        orderHolder.productQuantity.setText(String.valueOf(product.getQuantity()));

        orderHolder.lessQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrderManager.order != null && !mOrderManager.order.getStep().equals(Order.Step.NEW)) {
                    return;
                }

                lessQuantity(product);
                orderHolder.productQuantity.setText(String.valueOf(product.getQuantity()));
                orderHolder.productPrice.setText(UnitPriceUtils.addEuro(product.getPrice().toString()));
            }
        });

        orderHolder.moreQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrderManager.order != null && !mOrderManager.order.getStep().equals(Order.Step.NEW)) {
                    return;
                }

                moreQuantity(product);
                orderHolder.productQuantity.setText(String.valueOf(product.getQuantity()));
                orderHolder.productPrice.setText(UnitPriceUtils.addEuro(product.getPrice().toString()));
            }
        });
    }

    private void moreQuantity(Product product) {
        product = mOrderManager.addProduct(product);
        for (Product item : mItems) {
            if (item.getProductId().equals(product.getProductId())) {
                int index = mItems.indexOf(item);
                notifyItemChanged(index);
                break;
            }
        }

        updateOrder();
    }

    private void lessQuantity(Product product) {
        mOrderManager.removeProduct(product);

        Boolean isFind = false;

        for (Product prod : mItems) {
            if (prod.getProductId().equals(product.getProductId())) {
                isFind = true;
                int index = mItems.indexOf(prod);
                notifyItemChanged(index);
                break;
            }
        }

        if (!isFind) {
            notifyDataSetChanged();
        }

        updateOrder();
    }

    private void updateOrder() {
        mListener.onUpdateOrder();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface OnCartUpdate {
        void onUpdateOrder();
    }

    private static class ProductViewHolder extends RecyclerView.ViewHolder {
        Button lessQuantityButton;
        Button moreQuantityButton;
        TextView productQuantity;
        TextView productName;
        TextView productPrice;

        ProductViewHolder(View view) {
            super(view);

            lessQuantityButton = (Button) view.findViewById(R.id.lessQuantityButton);
            productQuantity = (TextView) view.findViewById(R.id.productQuantity);
            moreQuantityButton = (Button) view.findViewById(R.id.moreQuantityButton);
            productName = (TextView) view.findViewById(R.id.productName);
            productPrice = (TextView) view.findViewById(R.id.productPrice);
        }
    }
}
