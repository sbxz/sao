package com.sao.mobile.saopro.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.SaoBeacon;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.ui.activity.BeaconInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 29/12/2016.
 */

public class BeaconAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int REQUEST_CODE = 1;

    private List<SaoBeacon> mItems;
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public BeaconAdapter(Context context, List<SaoBeacon> items) {
        this.mContext = context;
        this.mItems = items != null ? items : new ArrayList<SaoBeacon>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }

        View view = mLayoutInflater.inflate(R.layout.item_beacon, parent, false);
        return new BeaconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final BeaconViewHolder beaconHolder = (BeaconViewHolder) holder;
        final SaoBeacon beacon = mItems.get(position);

        beaconHolder.name.setText(beacon.getName());
        beaconHolder.uuid.setText(beacon.getUuid());
        beaconHolder.major.setText(String.valueOf(beacon.getMajor()));
        beaconHolder.minor.setText(String.valueOf(beacon.getMinor()));

        beaconHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBeaconDetail(beaconHolder, beacon);
            }
        });
    }

    private void goToBeaconDetail(BeaconViewHolder beaconHolder, SaoBeacon beacon) {
        Activity activity = (Activity) mContext;
       /* ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(beaconHolder.uuid, BeaconInfoActivity.UUID_TRANSITION_NAME),
                new Pair(beaconHolder.major, BeaconInfoActivity.MAJOR_TRANSITION_NAME),
                new Pair(beaconHolder.minor, BeaconInfoActivity.MINOR_TRANSITION_NAME)
        );*/

        Intent intent = new Intent(activity, BeaconInfoActivity.class);
        intent.putExtra(BeaconInfoActivity.BEACON_EXTRA, beacon);
        activity.startActivity(intent);
    }

    public void addListItem(List<SaoBeacon> beacon) {
        mItems.addAll(beacon);
        notifyDataSetChanged();
    }

    public void addItem(SaoBeacon beacon) {
        mItems.add(beacon);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;

        public TextView uuid;
        public TextView major;
        public TextView minor;
        public TextView name;

        public BeaconViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            name = (TextView) view.findViewById(R.id.name);
            uuid = (TextView) view.findViewById(R.id.uuid);
            major = (TextView) view.findViewById(R.id.major);
            minor = (TextView) view.findViewById(R.id.minor);
        }
    }
}

