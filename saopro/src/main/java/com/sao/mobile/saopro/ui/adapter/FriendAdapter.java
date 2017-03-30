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


public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public FriendAdapter(Context context, List<User> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<User>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_friend, parent, false);
        return new FriendAdapter.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FriendAdapter.FriendViewHolder friendViewHolder = (FriendAdapter.FriendViewHolder) holder;
        User userFriend = (User) mItems.get(position);

        friendViewHolder.friendName.setText(userFriend.getName()+' ');
        friendViewHolder.friendLocalization.setText(userFriend.getName());

        int avatarSize = mContext.getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Picasso.with(mContext).load(userFriend.getThumbnail())
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .into(friendViewHolder.friendThumbnail);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addListItem(List<User> users) {
        mItems = users;
        notifyDataSetChanged();
    }

    private static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView friendThumbnail;
        TextView friendName;
        TextView friendLocalization;

        FriendViewHolder(View view) {
            super(view);
            friendThumbnail = (ImageView) view.findViewById(R.id.friendThumbnail);
            friendName = (TextView) view.findViewById(R.id.friendName);
            friendLocalization = (TextView) view.findViewById(R.id.friendLocalization);
        }
    }
}