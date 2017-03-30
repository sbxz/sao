package com.sao.mobile.sao.ui.adapter;

import android.annotation.SuppressLint;
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

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.ui.activity.BarActivity;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.adapter.LoadViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BarsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Bar> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public BarsAdapter(Context context, List<Bar> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<Bar>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        if (viewType == LoadViewHolder.VIEW_TYPE_ITEM) {
            return new BarsViewHolder(mLayoutInflater.inflate(R.layout.item_bar, parent, false));
        } else {
            return new LoadViewHolder(mLayoutInflater.inflate(R.layout.item_load, parent, false));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == LoadViewHolder.VIEW_TYPE_LOADING) {
            return;
        }

        final BarsViewHolder barsViewHolder = (BarsViewHolder) holder;
        final Bar bar = mItems.get(position);

        //barsViewHolder.barPoint.setText(bar.get() + " points");
        barsViewHolder.barName.setText(bar.getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Picasso.with(mContext).load(bar.getThumbnail())
                    .fit()
                    .centerCrop()
                    .into(barsViewHolder.barThumbnail);
        }

        barsViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBarDetail(barsViewHolder, bar);
            }
        });
    }

    private void goToBarDetail(BarsViewHolder barsViewHolder, Bar bar) {
        Activity activity = (Activity) mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(barsViewHolder.barThumbnail, BarActivity.IMAGE_TRANSITION_NAME),
                new Pair(barsViewHolder.barPoint, BarActivity.POINT_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, BarActivity.class);
        intent.putExtra(BarActivity.BAR_EXTRA, bar);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) == null ? LoadViewHolder.VIEW_TYPE_LOADING : LoadViewHolder.VIEW_TYPE_ITEM;
    }

    public void removeLoadItem() {
        mItems.remove(mItems.size() - 1);
        notifyDataSetChanged();
    }

    public void pushBar(List<Bar> bars) {
        mItems.addAll(bars);
        notifyDataSetChanged();
    }

    public void addLoadItem() {
        mItems.add(null);
        notifyItemInserted(mItems.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<Bar> bar) {
        mItems.addAll(bar);
        notifyDataSetChanged();
    }

    public void addItem(Bar bar) {
        mItems.add(bar);
        notifyDataSetChanged();
    }

    private static class BarsViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView barThumbnail;
        TextView barName;
        TextView barPoint;

        BarsViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            barThumbnail = (ImageView) view.findViewById(R.id.barThumbnail);
            barName = (TextView) view.findViewById(R.id.barName);
            barPoint = (TextView) view.findViewById(R.id.barPoint);
        }
    }
}
