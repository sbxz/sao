package com.sao.mobile.saolib.entities.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.TraderOrder;

/**
 * Created by Seb on 08/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyOrder {
    @SerializedName("bar")
    private Bar bar;
    @SerializedName("order")
    private TraderOrder order;

    public MyOrder() {
    }

    public MyOrder(Bar bar, TraderOrder order) {
        this.bar = bar;
        this.order = order;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public TraderOrder getOrder() {
        return order;
    }

    public void setOrder(TraderOrder order) {
        this.order = order;
    }
}
