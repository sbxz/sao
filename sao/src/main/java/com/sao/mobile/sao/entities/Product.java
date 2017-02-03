package com.sao.mobile.sao.entities;

import java.io.Serializable;

/**
 * Created by Seb on 23/01/2017.
 */

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private String price;
    private String quantity;

    public Product(String id, String name, String description, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = "0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        Integer quantityInt = Integer.parseInt(this.quantity);
        quantityInt++;
        quantity = quantityInt.toString();
    }
}
