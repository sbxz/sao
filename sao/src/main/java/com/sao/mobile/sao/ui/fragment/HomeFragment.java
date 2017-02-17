package com.sao.mobile.sao.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.ActuBar;
import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.Catalog;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.manager.OrderManager;
import com.sao.mobile.sao.manager.UserManager;
import com.sao.mobile.sao.ui.adapter.BarsAdapter;
import com.sao.mobile.sao.ui.adapter.HomeAdapter;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BaseFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    private View mView;

    private ImageView mBarThumbnail;
    private TextView mBarName;
    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ProgressBar mLoadProgressBar;

    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private OrderManager mOrderManager = OrderManager.getInstance();
    private UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void initServices() {

    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        setupCurrentBar();
        setupRecyclerView();

        return mView;
    }

    private void setupCurrentBar() {
        mBarThumbnail = (ImageView) mView.findViewById(R.id.barThumbnail);
        mBarName = (TextView) mView.findViewById(R.id.barName);
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.homeRecycerView);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mRecyclerView.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mRecyclerView.addOnScrollListener(mEndlessRecyclerScrollListener);
        mHomeAdapter = new HomeAdapter(mContext, fake());
        mRecyclerView.setAdapter(mHomeAdapter);
    }

    private List<ActuBar> fake() {
        List<Bar> bars = parseData();
        List<ActuBar> barActus = new ArrayList<>();
        barActus.add(new ActuBar(bars.get(0), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(1), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(2), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(3), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(4), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(5), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(0), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(1), "ergregreger", "rgergregreg"));
        barActus.add(new ActuBar(bars.get(2), "ergregreger", "rgergregreg"));

        return barActus;
    }

    private List<Bar> parseData() {
        List<Product> products = new ArrayList<Product>();
        products.add(new Product("1", "Bière", "Plein de chose", "2"));
        products.add(new Product("2", "Café", "Plein de chose","3"));
        products.add(new Product("3", "Vodka", "Plein de chose","5"));
        products.add(new Product("4", "Grim", "Plein de chose","10"));
        products.add(new Product("5", "Pastis", "Plein de chose","7"));
        products.add(new Product("6", "Ricard", "Plein de chose","6"));
        products.add(new Product("7", "Eau", "Plein de chose","9"));

        List<Product> products1 = new ArrayList<Product>();
        products1.add(new Product("8", "Bière", "Plein de chose", "4"));
        products1.add(new Product("9", "Café", "Plein de chose","5"));
        products1.add(new Product("10", "Vodka", "Plein de chose","7"));
        products1.add(new Product("11", "Grim", "Plein de chose","3"));
        products1.add(new Product("12", "Pastis", "Plein de chose","2"));
        products1.add(new Product("13", "Ricard", "Plein de chose","5"));
        products1.add(new Product("14", "Eau", "Plein de chose","10"));

        List<Product> products2 = new ArrayList<Product>();
        products2.add(new Product("15", "Bière", "Plein de chose", "10"));
        products2.add(new Product("16", "Café", "Plein de chose","5"));
        products2.add(new Product("17", "Vodka", "Plein de chose","8"));
        products2.add(new Product("18", "Grim", "Plein de chose","7"));
        products2.add(new Product("19", "Pastis", "Plein de chose","3"));
        products2.add(new Product("20", "Ricard", "Plein de chose","2"));
        products2.add(new Product("21", "Eau", "Plein de chose","1"));

        List<Product> products3 = new ArrayList<Product>();
        products3.add(new Product("22", "Bière", "Plein de chose", "10"));
        products3.add(new Product("23", "Café", "Plein de chose","5"));
        products3.add(new Product("24", "Vodka", "Plein de chose","8"));
        products3.add(new Product("25", "Grim", "Plein de chose","7"));
        products3.add(new Product("26", "Pastis", "Plein de chose","3"));
        products3.add(new Product("27", "Ricard", "Plein de chose","2"));
        products3.add(new Product("28", "Eau", "Plein de chose","1"));

        List<Product> products4 = new ArrayList<Product>();
        products4.add(new Product("29", "Bière", "Plein de chose", "10"));
        products4.add(new Product("30", "Café", "Plein de chose","5"));
        products4.add(new Product("31", "Vodka", "Plein de chose","8"));
        products4.add(new Product("34", "Grim", "Plein de chose","7"));
        products4.add(new Product("35", "Pastis", "Plein de chose","3"));
        products4.add(new Product("36", "Ricard", "Plein de chose","2"));
        products4.add(new Product("37", "Eau", "Plein de chose","1"));

        List<Product> products5 = new ArrayList<Product>();
        products5.add(new Product("38", "Bière", "Plein de chose", "10"));
        products5.add(new Product("39", "Café", "Plein de chose","5"));
        products5.add(new Product("40", "Vodka", "Plein de chose","8"));
        products5.add(new Product("41", "Grim", "Plein de chose","7"));
        products5.add(new Product("42", "Pastis", "Plein de chose","3"));
        products5.add(new Product("43", "Ricard", "Plein de chose","2"));
        products5.add(new Product("44", "Eau", "Plein de chose","1"));

        List<Product> products6 = new ArrayList<Product>();
        products6.add(new Product("45", "Bière", "Plein de chose", "10"));
        products6.add(new Product("46", "Café", "Plein de chose","5"));
        products6.add(new Product("47", "Vodka", "Plein de chose","8"));
        products6.add(new Product("48", "Grim", "Plein de chose","7"));
        products6.add(new Product("49", "Pastis", "Plein de chose","3"));
        products6.add(new Product("50", "Ricard", "Plein de chose","2"));
        products6.add(new Product("51", "Eau", "Plein de chose","1"));

        List<Catalog> catalog = new ArrayList<Catalog>();
        catalog.add(new Catalog("Les Softs", products));
        catalog.add(new Catalog("Les Klassic", products1));
        catalog.add(new Catalog("Les Supérior", products2));
        catalog.add(new Catalog("Les Bières", products3));
        catalog.add(new Catalog("Les Chaud", products4));
        catalog.add(new Catalog("Cocktails", products5));
        catalog.add(new Catalog("Shooters", products6));

        List<Bar> bars = new ArrayList<Bar>();
        bars.add(new Bar("1", "La kolok", "http://i.imgur.com/CqmBjo5.jpg", "", "", "100", catalog));
        bars.add(new Bar("2", "Red house", "http://i.imgur.com/9gbQ7YR.jpg", "", "", "100", catalog));
        bars.add(new Bar("3", "Petit Salon", "http://i.imgur.com/P5JLfjk.jpg", "", "", "100", catalog));
        bars.add(new Bar("4", "Peroquet bourré", "http://i.imgur.com/FI49ftb.jpg", "", "", "100", catalog));
        bars.add(new Bar("5", "L'abreuvoir", "http://i.imgur.com/yAdbrLp.jpg", "", "", "100", catalog));
        bars.add(new Bar("6", "Shamrock", "http://i.imgur.com/DAl0KB8.jpg", "", "", "100", catalog));
        return bars;
    }
}
