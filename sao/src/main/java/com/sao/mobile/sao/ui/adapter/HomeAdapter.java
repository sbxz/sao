package com.sao.mobile.sao.ui.adapter;

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
import com.sao.mobile.sao.entities.ActuBar;
import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.ui.activity.BarDetailActivity;
import com.sao.mobile.saolib.utils.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 15/02/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ActuBar> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public HomeAdapter(Context context, List<ActuBar> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<ActuBar>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final HomeViewHolder homeViewHolder = (HomeViewHolder) holder;
        ActuBar actuBar = (ActuBar) mItems.get(position);
        final Bar bar = actuBar.getBar();

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Picasso.with(mContext).load(bar.getBarThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(homeViewHolder.barThumbnail);

        homeViewHolder.barText.setText(actuBar.getText());
        homeViewHolder.barName.setText(bar.getBarName());

        homeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBarDetail(homeViewHolder, bar);
            }
        });

    }

    private void goToBarDetail(HomeViewHolder barsViewHolder, Bar bar) {
        Activity activity = (Activity) mContext;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(barsViewHolder.barThumbnail, BarDetailActivity.IMAGE_TRANSITION_NAME)
        );

        Intent intent = new Intent(activity, BarDetailActivity.class);
        intent.putExtra(BarDetailActivity.BAR_EXTRA, bar);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class HomeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView barThumbnail;
        TextView barName;
        TextView date;
        TextView barText;

        HomeViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            barThumbnail = (ImageView) view.findViewById(R.id.barThumbnail);
            barName = (TextView) view.findViewById(R.id.barName);
            date = (TextView) view.findViewById(R.id.date);
            barText = (TextView) view.findViewById(R.id.barText);
        }
    }
}
