package com.sao.mobile.saolib.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 24/01/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Serializable {
    public enum Step {
        NEW, INPROGRESS, READY, VALIDATE
    }

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("barId")
    private Long barId;

    @SerializedName("facebookUserId")
    private String facebookUserId;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("totalQuantity")
    private int totalQuantity;

    @SerializedName("step")
    private Step step;

    @SerializedName("products")
    private List<Product> products;

    public Order() {
    }

    public Order(String facebookUserId,Long barId, double totalPrice, List<Product> products) {
        this.facebookUserId = facebookUserId;
        this.barId = barId;
        this.totalQuantity = 1;
        this.totalPrice = totalPrice;
        this.products = products;
        this.step = Step.NEW;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBarId() {
        return barId;
    }

    public void setBarId(Long barId) {
        this.barId = barId;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity < 0 ? 0 : totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice < 0 ? 0 : totalPrice;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void incrementQuantity() {
        totalQuantity++;
    }

    public void decrementQuantity() {
        if(totalQuantity != 0) {
            totalQuantity--;
        }
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }
}
