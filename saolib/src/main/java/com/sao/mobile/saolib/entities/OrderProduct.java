package com.sao.mobile.saolib.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seb on 05/03/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderProduct implements Serializable {
    @SerializedName("product")
    private Product product;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private Double price;

    public OrderProduct() {
    }

    public OrderProduct(Product product, int quantity, Double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
