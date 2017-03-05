package com.sao.mobile.saopro.ui.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.TraderOrder;
import com.sao.mobile.saopro.manager.ApiManager;
import com.sao.mobile.saopro.manager.TraderManager;
import com.sao.mobile.saopro.service.SaoMessagingService;
import com.sao.mobile.saopro.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sao.mobile.saopro.ui.activity.OrderDetailsActivity.ORDER_EXTRA;

public class OrderListFragment extends BaseFragment {
    private static final String TAG = OrderListFragment.class.getSimpleName();

    private View mView;
    private ViewPager mViewPager;
    private TabLayout mOrderTabs;

    private InProgressFragment mInProgressFragment;
    private ReadyFragment mReadyFragment;

    private ApiManager mApiManager = ApiManager.getInstance();
    private TraderManager mTraderManager = TraderManager.getInstance();

    private int[] tabIcons = {
            R.drawable.ic_menu_camera,
            R.drawable.ic_menu_send
    };

    public OrderListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order_list, container, false);

        setupTabs();
        refreshOrderList();

        return mView;
    }

    private void setupTabs() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);

        mInProgressFragment = new InProgressFragment();
        mReadyFragment = new ReadyFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(mContext.getSupportFragmentManager());
        adapter.addFragment(mInProgressFragment, "Nouvelles");
        adapter.addFragment(mReadyFragment, "En attente");
        mViewPager.setAdapter(adapter);

        mOrderTabs = (TabLayout) mView.findViewById(R.id.orderTabs);
        mOrderTabs.setupWithViewPager(mViewPager);

        mOrderTabs.getTabAt(0).setIcon(tabIcons[0]);
        mOrderTabs.getTabAt(1).setIcon(tabIcons[1]);

        mOrderTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void refreshOrderList() {
        Call<Map<String, List<TraderOrder>>> barServiceCall = mApiManager.barService.retrieveOrderList(mTraderManager.currentBar.getBarId());
        barServiceCall.enqueue(new Callback<Map<String, List<TraderOrder>>>() {
            @Override
            public void onResponse(Call<Map<String, List<TraderOrder>>> call, Response<Map<String, List<TraderOrder>>> response) {
                hideProgressLoad();
                if (response.code() != 200) {
                    Log.i(TAG, "Fail retrieve order");
                    return;
                }

                Log.i(TAG, "Success retrieve orders");
                mInProgressFragment.addListOrder(response.body().get("inProgress"));
                mReadyFragment.addListOrder(response.body().get("ready"));
            }

            @Override
            public void onFailure(Call<Map<String, List<TraderOrder>>> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve orders.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void shopwProgressLoad() {
        mInProgressFragment.showProgressLoad();
        mReadyFragment.showProgressLoad();
    }

    private void hideProgressLoad() {
        mInProgressFragment.hideProgressLoad();
        mReadyFragment.hideProgressLoad();
    }

    public void updateOrderList(Intent intent) {
        TraderOrder order = (TraderOrder) intent.getSerializableExtra(ORDER_EXTRA);
        if (order.getStep().equals(Order.Step.READY)) {
            mReadyFragment.addOrder(order);
            mInProgressFragment.removeOrder(order);
        } else if (order.getStep().equals(Order.Step.VALIDATE)) {
            mReadyFragment.removeOrder(order);
        }
    }

    public void removeFragment() {
        if (mInProgressFragment == null) {
            return;
        }

        FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(mInProgressFragment);
        fragmentTransaction.remove(mReadyFragment);
        fragmentTransaction.commit();
    }

    public void addOrder(TraderOrder traderOrder) {
        if(mInProgressFragment == null) {
            return;
        }

        mInProgressFragment.addOrder(traderOrder);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
