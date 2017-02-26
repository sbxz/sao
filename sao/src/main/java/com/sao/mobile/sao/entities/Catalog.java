package com.sao.mobile.sao.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 23/01/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Catalog implements Serializable {
    private String type;
    private List<Product> products;

    public Catalog() {
    }

    public Catalog(String type, List<Product> products) {
        this.type = type;
        this.products = products;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
