package com.sao.mobile.sao.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.saolib.entities.News;
import com.sao.mobile.saolib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 07/03/2017.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public NewsAdapter(Context context, List<News> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<News>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_news, parent, false);

        return new NewsAdapter.NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewsAdapter.NewsViewHolder newsViewHolder = (NewsAdapter.NewsViewHolder) holder;
        News news = (News) mItems.get(position);

        newsViewHolder.date.setText(Utils.getRelativeTime(news.getCreated()));
        newsViewHolder.content.setText(news.getContent());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<News> users) {
        mItems = users;
        notifyDataSetChanged();
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView content;

        NewsViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            content = (TextView) view.findViewById(R.id.content);
        }
    }
}
