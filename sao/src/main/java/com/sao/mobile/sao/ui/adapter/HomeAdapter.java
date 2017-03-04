package com.sao.mobile.sao.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.ui.activity.BarDetailActivity;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.News;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 15/02/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public HomeAdapter(Context context, List<News> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<News>();
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
        News news = (News) mItems.get(position);
        final Bar bar = news.getBar();

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Picasso.with(mContext).load(bar.getThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .into(homeViewHolder.barThumbnail);

        long now = System.currentTimeMillis();
        CharSequence relavetime = DateUtils.getRelativeTimeSpanString(
                news.getCreated(),
                now,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);

        homeViewHolder.date.setText(relavetime);

        homeViewHolder.barText.setText(news.getContent());
        homeViewHolder.barName.setText(bar.getName());

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

    public void addListItem(List<News> newsList) {
        mItems = newsList;
        notifyDataSetChanged();
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
