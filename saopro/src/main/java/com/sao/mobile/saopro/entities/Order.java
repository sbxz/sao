package com.sao.mobile.saopro.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 01/12/2016.
 */

public class Order implements Serializable {
    public enum Step {
        FINISH, WAIT, START
    }

    private String id;
    private Customer customer;
    private String totalPrice;
    private List<Product> products;
    private String date;
    private Step step;

    public Order(String id, Customer customer, String totalPrice, List<Product> products, String date, Step step) {
        this.id = id;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.products = products;
        this.date = date;
        this.step = step;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }
}
