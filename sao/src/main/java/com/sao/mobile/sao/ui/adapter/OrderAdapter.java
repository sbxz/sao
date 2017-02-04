package com.sao.mobile.sao.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.saolib.utils.UnitPriceUtils;

import java.util.ArrayList;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private OrderManager mOrderManager = OrderManager.getInstance();

    public OrderAdapter(Context context, List<Product> items) {
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
        orderHolder.productPrice.setText(UnitPriceUtils.addEuro(String.valueOf(Double.parseDouble(product.getPrice()) * Double.parseDouble(product.getQuantity()))));
        orderHolder.productQuantity.setText(product.getQuantity());

        orderHolder.lessQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lessQuantity(product);
                orderHolder.productQuantity.setText(product.getQuantity());
                orderHolder.productPrice.setText(UnitPriceUtils.addEuro(product.getPrice()));
            }
        });

        orderHolder.moreQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreQuantity(product);
                orderHolder.productQuantity.setText(product.getQuantity());
                orderHolder.productPrice.setText(UnitPriceUtils.addEuro(product.getPrice()));
            }
        });
    }

    private void moreQuantity(Product product) {
        product = mOrderManager.addProduct(product);
        for (Product item : mItems) {
            if(item.getId().equals(product.getId())) {
                int index = mItems.indexOf(item);
                notifyItemChanged(index);

                return;
            }
        }
    }

    private void lessQuantity(Product product) {
        Product productTemp = mOrderManager.removeProduct(product);
        for (Product prod : mItems) {
            if(prod.getId() == product.getId()) {
                int index = mItems.indexOf(prod);

                if(productTemp == null) {
                    mItems.remove(index);
                    notifyItemRemoved(index);
                    return;
                }

                notifyItemChanged(index);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public Button lessQuantityButton;
        public Button moreQuantityButton;
        public TextView productQuantity;
        public TextView productName;
        public TextView productPrice;

        public ProductViewHolder(View view) {
            super(view);

            lessQuantityButton = (Button) view.findViewById(R.id.lessQuantityButton);
            productQuantity = (TextView) view.findViewById(R.id.productQuantity);
            moreQuantityButton = (Button) view.findViewById(R.id.moreQuantityButton);
            productName = (TextView) view.findViewById(R.id.productName);
            productPrice = (TextView) view.findViewById(R.id.productPrice);
        }
    }
}
