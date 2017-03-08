package com.sao.mobile.saolib.entities.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Order;

/**
 * Created by Seb on 08/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyOrder {
    @SerializedName("bar")
    private Bar bar;
    @SerializedName("order")
    private Order order;

    public MyOrder() {
    }

    public MyOrder(Bar bar, Order order) {
        this.bar = bar;
        this.order = order;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
