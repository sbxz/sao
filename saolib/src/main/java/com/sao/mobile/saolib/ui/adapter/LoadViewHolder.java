package com.sao.mobile.saolib.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Seb on 30/03/2017.
 */

public class LoadViewHolder extends RecyclerView.ViewHolder {
    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    public LoadViewHolder(View itemView) {
        super(itemView);
    }
}