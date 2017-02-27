package com.sao.mobile.saopro.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
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

import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.utils.LoggerUtils;
import com.sao.mobile.saolib.utils.SnackBarUtils;
import com.sao.mobile.saopro.R;
import com.sao.mobile.saopro.entities.Customer;
import com.sao.mobile.saopro.entities.Order;
import com.sao.mobile.saopro.entities.Product;
import com.sao.mobile.saopro.manager.ApiManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sao.mobile.saopro.ui.activity.OrderDetailsActivity.ORDER_EXTRA;

public class OrderListFragment extends BaseFragment {
    private static final String TAG = OrderListFragment.class.getSimpleName();

    public static final String NEW_ORDER_EXTRA = "new_order_extra";

    private View mView;
    private ViewPager mViewPager;
    private TabLayout mOrderTabs;

    private BroadcastReceiver mBroadcastReceiver;

    private OrderNewListFragment mOrderNewListFragment;
    private OrderWaitListFragment mOrderWaitListFragment;

    private ApiManager mApiManager = ApiManager.getInstance();

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
        registerBroadcastReceiver();

        return mView;
    }

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NEW_ORDER_EXTRA)) {
                    List<Product> products = new ArrayList<Product>();
                    products.add(new Product("Bière", "5", "2"));
                    products.add(new Product("Coca", "5", "2"));
                    products.add(new Product("Cafe", "5", "2"));
                    mOrderNewListFragment.addOrder(new Order("2", new Customer("", "", "Seb", "http://i.imgur.com/FI49ftb.jpg"), "15", products, "18:58", Order.Step.START));
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(NEW_ORDER_EXTRA));
    }

    private void setupTabs() {
        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);

        mOrderNewListFragment = new OrderNewListFragment();
        mOrderWaitListFragment = new OrderWaitListFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(mContext.getSupportFragmentManager());
        adapter.addFragment(mOrderNewListFragment, "Nouvelles");
        adapter.addFragment(mOrderWaitListFragment, "En attente");
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
        Call<Void> barServiceCall = mApiManager.barService.retrieveOrderList();
        barServiceCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success retrieve orders");
                hideProgressLoad();
                List<Product> products = new ArrayList<Product>();
                products.add(new Product("Bière", "5", "2"));
                products.add(new Product("Coca", "5", "2"));
                products.add(new Product("Cafe", "5", "2"));
                List<Order> orders = new ArrayList<Order>();
                orders.add(new Order("1", new Customer("", "", "Sebas", "http://i.imgur.com/FI49ftb.jpg"), "15", products, "18:58", Order.Step.START));

                mOrderNewListFragment.addListOrder(orders);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideProgressLoad();
                LoggerUtils.apiFail(TAG, "Fail retrieve orders.", t);
                SnackBarUtils.showSnackError(getView());
            }
        });
    }

    private void shopwProgressLoad() {
        mOrderNewListFragment.showProgressLoad();
        mOrderWaitListFragment.showProgressLoad();
    }

    private void hideProgressLoad() {
        mOrderNewListFragment.hideProgressLoad();
        mOrderWaitListFragment.hideProgressLoad();
    }

    public void updateOrderList(Intent intent) {
        Order order = (Order) intent.getSerializableExtra(ORDER_EXTRA);
        if(order.getStep().equals(Order.Step.WAIT)) {
            mOrderWaitListFragment.addOrder(order);
            mOrderNewListFragment.removeOrder(order);
        } else if(order.getStep().equals(Order.Step.FINISH)) {
            mOrderWaitListFragment.removeOrder(order);
        }
    }

    public void removeFragment() {
        if(mOrderNewListFragment == null) {
            return;
        }

        FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(mOrderNewListFragment);
        fragmentTransaction.remove(mOrderWaitListFragment);
        fragmentTransaction.commit();
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
