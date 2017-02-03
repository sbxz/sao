package com.sao.mobile.sao.manager;

import com.sao.mobile.sao.entities.Order;
import com.sao.mobile.sao.entities.Product;
import com.sao.mobile.saolib.utils.UnitPriceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 25/01/2017.
 */
public class OrderManager {
    private static OrderManager ourInstance = new OrderManager();

    public static OrderManager getInstance() {
        return ourInstance;
    }

    public Order order;

    private OrderManager() {
    }

    public void addProduct(Product product) {
        if(order == null) {
            List<Product> products = new ArrayList<>();
            products.add(product);
            order = new Order(calculPrice(product), products);
        } else {
            for(Product item : order.getProducts()) {

            }
            verifyProduct(product);
            order.getProducts().add(product);
            order.incrementQuantity();
            order.setTotalPrice(order.getTotalPrice() + calculPrice(product));
        }
    }

    private void verifyProduct(Product product) {

    }

    public void removeProduct(Product product) {

    }

    public void removeOrder() {
        order = null;
    }

    private double calculPrice(Product product) {
        return Double.parseDouble(product.getPrice());
    }

    public String getTotalQuantityAsString() {
        return String.valueOf(order.getTotalQuantity());
    }

    public String getTotalPriceAsString() {
        return UnitPriceUtils.addEuro(String.valueOf(order.getTotalPrice()));
    }
}
