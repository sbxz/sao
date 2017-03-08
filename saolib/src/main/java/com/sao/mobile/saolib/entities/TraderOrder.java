package com.sao.mobile.saolib.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 05/03/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraderOrder implements Serializable {
    @SerializedName("orderId")
    private Long orderId;
    @SerializedName("user")
    private User user;
    @SerializedName("totalQuantity")
    private int totalQuantity;
    @SerializedName("totalPrice")
    private Double totalPrice;
    @SerializedName("step")
    private Order.Step step;
    @SerializedName("orderProducts")
    private List<OrderProduct> orderProducts;
    @SerializedName("date")
    private Long date;

    public TraderOrder() {
    }

    public TraderOrder(Long orderId, User user, int totalQuantity, Double totalPrice, Order.Step step, List<OrderProduct> orderProducts, Long date) {
        this.orderId = orderId;
        this.user = user;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.step = step;
        this.orderProducts = orderProducts;
        this.date = date;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Order.Step getStep() {
        return step;
    }

    public void setStep(Order.Step step) {
        this.step = step;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
