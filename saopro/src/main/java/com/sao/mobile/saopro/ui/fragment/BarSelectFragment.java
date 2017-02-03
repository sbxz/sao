package com.sao.mobile.saopro.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.frameLayout.DragLayout;
import com.sao.mobile.saolib.utils.LocalStore;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Bar;
import com.sao.mobile.saopro.ui.MainActivity;
import com.squareup.picasso.Picasso;

public class BarSelectFragment extends BaseFragment {
    private static final String TAG = BarSelectFragment.class.getSimpleName();

    private ImageView mImageView;
    private TextView mAddress;
    private TextView mPhoneNumber;

    private Bar bar;

    public BarSelectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_select, container, false);

        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        mAddress = (TextView) dragLayout.findViewById(R.id.address);
        mPhoneNumber = (TextView) dragLayout.findViewById(R.id.phoneNumber);
        mImageView = (ImageView) dragLayout.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(bar.getThumbnail())
                .placeholder(R.drawable.sao)
                .fit()
                .centerCrop()
                .into(mImageView);

        mAddress.setText(bar.getAddress());
        mPhoneNumber.setText(bar.getPhoneNumber());

        dragLayout.setGotoBarListener(new DragLayout.GotoBarListener() {
            @Override
            public void gotoBar() {
                LocalStore.writePreferences(mContext, LocalStore.CURRENT_BAR_ID, bar.getId());
                getActivity().finish();
                startActivity(MainActivity.class);
            }
        });

        return rootView;
    }

    public void bindData(Bar bar) {
        this.bar = bar;
    }

    @Override
    protected void initServices() {

    }
}

