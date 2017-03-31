package com.sao.mobile.saopro.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.User;
import com.sao.mobile.saopro.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AVA on 30/03/2017.
 */


public class CustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public CustomerAdapter(Context context, List<User> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<User>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CustomerViewHolder customerViewHolder = (CustomerViewHolder) holder;
        User user = (User) mItems.get(position);

        customerViewHolder.userName.setText(user.getName());
        customerViewHolder.userInfo.setText("");

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Picasso.with(mContext).load(user.getThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .into(customerViewHolder.userThumbnail);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<User> users) {
        mItems = users;
        notifyDataSetChanged();
    }

    private static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView userThumbnail;
        TextView userName;
        TextView userInfo;

        CustomerViewHolder(View view) {
            super(view);
            userThumbnail = (ImageView) view.findViewById(R.id.userThumbnail);
            userName = (TextView) view.findViewById(R.id.userName);
            userInfo = (TextView) view.findViewById(R.id.userInfo);
        }
    }
}