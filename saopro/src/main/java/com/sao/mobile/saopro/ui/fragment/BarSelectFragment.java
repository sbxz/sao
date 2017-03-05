package com.sao.mobile.saopro.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.frameLayout.DragLayout;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.manager.TraderManager;
import com.sao.mobile.saopro.ui.MainActivity;
import com.squareup.picasso.Picasso;

public class BarSelectFragment extends BaseFragment {
    private static final String TAG = BarSelectFragment.class.getSimpleName();

    private Bar bar;

    private TraderManager mTraderManager = TraderManager.getInstance();

    public BarSelectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_select, container, false);

        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        TextView address = (TextView) dragLayout.findViewById(R.id.address);
        TextView phoneNumber = (TextView) dragLayout.findViewById(R.id.phoneNumber);
        ImageView imageView = (ImageView) dragLayout.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(bar.getThumbnail())
                .placeholder(R.drawable.sao)
                .fit()
                .centerCrop()
                .into(imageView);

        address.setText(bar.getAddress());
        phoneNumber.setText(bar.getPhoneNumber());

        dragLayout.setGotoBarListener(new DragLayout.GotoBarListener() {
            @Override
            public void gotoBar() {
                mTraderManager.currentBar = bar;
                LocalStore.writePreferences(mContext, LocalStore.TRADER_BAR_ID, bar.getBarId().toString());
                getActivity().finish();
                startActivity(MainActivity.class);
            }
        });

        return rootView;
    }

    public void bindData(Bar bar) {
        this.bar = bar;
    }
}

