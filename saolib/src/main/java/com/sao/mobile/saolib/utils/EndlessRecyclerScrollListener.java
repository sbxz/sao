package com.sao.mobile.saolib.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerScrollListener extends RecyclerView.OnScrollListener {

    private static final long VISIBLE_THRESHOLD = 1;
    public boolean isMoreDataAvailable = true;
    private boolean isLoading = true;
    private int current_page = 0;
    private int lastVisibleItem, totalItemCount;
    private LinearLayoutManager linearLayoutManager;

    public EndlessRecyclerScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

        if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
            current_page++;
            onLoadMore(current_page);
            isLoading = true;
        }
    }

    public void clean() {
        isLoading = true;
        totalItemCount = 0;
        current_page = 1;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void resetLoading() {
        isLoading = false;
    }

    public abstract void onLoadMore(int currentPage);

}