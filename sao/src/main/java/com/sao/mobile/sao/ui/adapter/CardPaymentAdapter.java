package com.sao.mobile.sao.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.CardPayment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 09/03/2017.
 */

public class CardPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CardPayment> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public CardPaymentAdapter(Context context, List<CardPayment> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<CardPayment>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_card_payement, parent, false);
        return new CardPaymentAdapter.CardPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CardPaymentAdapter.CardPaymentViewHolder cardViewHolder = (CardPaymentAdapter.CardPaymentViewHolder) holder;
        CardPayment cardPayment = (CardPayment) mItems.get(position);

        cardViewHolder.name.setText(cardPayment.getName());
        cardViewHolder.cardNumber.setText("**** **** **** " + cardPayment.getNumber().substring(16));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<CardPayment> cardPayments) {
        mItems = cardPayments;
        notifyDataSetChanged();
    }

    private static class CardPaymentViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView cardNumber;

        CardPaymentViewHolder(View view) {
            super(view);
            cardNumber = (TextView) view.findViewById(R.id.card_number);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}
