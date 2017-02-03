package com.sao.mobile.sao.entities;

import java.util.List;

/**
 * Created by Seb on 24/01/2017.
 */

public class Order {
    private int totalQuantity;
    private double totalPrice;
    private List<Product> products;

    public Order(double totalPrice, List<Product> products) {
        this.totalQuantity = 1;
        this.totalPrice = totalPrice;
        this.products = products;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
        totalQuantity--;
    }
}
