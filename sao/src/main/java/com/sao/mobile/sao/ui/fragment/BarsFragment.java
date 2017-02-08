package com.sao.mobile.sao.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sao.mobile.sao.R;
import com.sao.mobile.sao.entities.Bar;
import com.sao.mobile.sao.entities.Catalog;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.sao.service.api.UserService;
import com.sao.mobile.sao.ui.adapter.BarsAdapter;
import com.sao.mobile.saolib.ui.base.BaseFragment;
import com.sao.mobile.saolib.ui.recyclerView.PreCachingLayoutManager;
import com.sao.mobile.saolib.utils.DeviceUtils;
import com.sao.mobile.saolib.utils.EndlessRecyclerScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarsFragment extends BaseFragment {
    private static final String TAG = BarsFragment.class.getSimpleName();

    private RecyclerView mBarRecyler;
    private BarsAdapter mBarAdapter;
    private ProgressBar mLoadProgressBar;
    private View mView;

    private LinearLayoutManager mLayoutManager;
    private EndlessRecyclerScrollListener mEndlessRecyclerScrollListener;

    private UserService mUserService;

    public BarsFragment() {
    }

    @Override
    protected void initServices() {
        mUserService = retrofit.create(UserService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bars, container, false);
        mLoadProgressBar = (ProgressBar) mView.findViewById(R.id.loadProgressBar);
        
        initRecyclerView();
        refreshBarsList();

        return mView;
    }

    private void initRecyclerView() {
        mBarRecyler = (RecyclerView) mView.findViewById(R.id.barRecycler);
        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));
        mBarRecyler.setLayoutManager(layoutManager);

        mEndlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };

        mBarRecyler.addOnScrollListener(mEndlessRecyclerScrollListener);
        mBarAdapter = new BarsAdapter(mContext, null);
        mBarRecyler.setAdapter(mBarAdapter);
    }

    private void refreshBarsList() {
        Call<Void> barsCall = mUserService.retrieveBars();
        barsCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Success retrieve bars");
                hideProgressLoad();
                List<Bar> bars = parseData();
                mBarAdapter.addListItem(bars);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideProgressLoad();
                Log.e(TAG, "Fail retrieve user bar. Message= " + t.getMessage());
                Snackbar.make(getView(), R.string.failure_data, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void hideProgressLoad() {
        mLoadProgressBar.setVisibility(View.GONE);
        mBarRecyler.setVisibility(View.VISIBLE);
    }

    private void showProgressLoad() {
        mLoadProgressBar.setVisibility(View.VISIBLE);
        mBarRecyler.setVisibility(View.GONE);
    }

    private List<Bar> parseData() {
        List<Product> products = new ArrayList<Product>();
        products.add(new Product("1", "Bière", "Plein de chose", "13"));
        products.add(new Product("2", "Café", "Plein de chose","10"));
        products.add(new Product("3", "Vodka", "Plein de chose","8"));
        products.add(new Product("4", "Grim", "Plein de chose","15"));
        products.add(new Product("5", "Pastis", "Plein de chose","20"));
        products.add(new Product("6", "Ricard", "Plein de chose","150"));
        products.add(new Product("7", "Eau", "Plein de chose","1"));

        List<Product> products1 = new ArrayList<Product>();
        products1.add(new Product("8", "Bière", "Plein de chose", "13"));
        products1.add(new Product("9", "Café", "Plein de chose","10"));
        products1.add(new Product("10", "Vodka", "Plein de chose","8"));
        products1.add(new Product("11", "Grim", "Plein de chose","15"));
        products1.add(new Product("12", "Pastis", "Plein de chose","20"));
        products1.add(new Product("13", "Ricard", "Plein de chose","150"));
        products1.add(new Product("14", "Eau", "Plein de chose","1"));

        List<Product> products2 = new ArrayList<Product>();
        products2.add(new Product("15", "Bière", "Plein de chose", "13"));
        products2.add(new Product("16", "Café", "Plein de chose","10"));
        products2.add(new Product("17", "Vodka", "Plein de chose","8"));
        products2.add(new Product("18", "Grim", "Plein de chose","15"));
        products2.add(new Product("19", "Pastis", "Plein de chose","20"));
        products2.add(new Product("20", "Ricard", "Plein de chose","150"));
        products2.add(new Product("21", "Eau", "Plein de chose","1"));

        List<Product> products3 = new ArrayList<Product>();
        products3.add(new Product("22", "Bière", "Plein de chose", "13"));
        products3.add(new Product("23", "Café", "Plein de chose","10"));
        products3.add(new Product("24", "Vodka", "Plein de chose","8"));
        products3.add(new Product("25", "Grim", "Plein de chose","15"));
        products3.add(new Product("26", "Pastis", "Plein de chose","20"));
        products3.add(new Product("27", "Ricard", "Plein de chose","150"));
        products3.add(new Product("28", "Eau", "Plein de chose","1"));

        List<Product> products4 = new ArrayList<Product>();
        products4.add(new Product("29", "Bière", "Plein de chose", "13"));
        products4.add(new Product("30", "Café", "Plein de chose","10"));
        products4.add(new Product("31", "Vodka", "Plein de chose","8"));
        products4.add(new Product("34", "Grim", "Plein de chose","15"));
        products4.add(new Product("35", "Pastis", "Plein de chose","20"));
        products4.add(new Product("36", "Ricard", "Plein de chose","150"));
        products4.add(new Product("37", "Eau", "Plein de chose","1"));

        List<Product> products5 = new ArrayList<Product>();
        products5.add(new Product("38", "Bière", "Plein de chose", "13"));
        products5.add(new Product("39", "Café", "Plein de chose","10"));
        products5.add(new Product("40", "Vodka", "Plein de chose","8"));
        products5.add(new Product("41", "Grim", "Plein de chose","15"));
        products5.add(new Product("42", "Pastis", "Plein de chose","20"));
        products5.add(new Product("43", "Ricard", "Plein de chose","150"));
        products5.add(new Product("44", "Eau", "Plein de chose","1"));

        List<Product> products6 = new ArrayList<Product>();
        products6.add(new Product("45", "Bière", "Plein de chose", "13"));
        products6.add(new Product("46", "Café", "Plein de chose","10"));
        products6.add(new Product("47", "Vodka", "Plein de chose","8"));
        products6.add(new Product("48", "Grim", "Plein de chose","15"));
        products6.add(new Product("49", "Pastis", "Plein de chose","20"));
        products6.add(new Product("50", "Ricard", "Plein de chose","150"));
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
