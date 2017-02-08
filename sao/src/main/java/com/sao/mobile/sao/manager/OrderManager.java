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

    public Product addProduct(Product product) {
        if(order == null) {
            List<Product> products = new ArrayList<>();
            products.add(product);
            order = new Order(calculPrice(product), products);
        } else {
            Boolean isFind = false;
            for(Product item : order.getProducts()) {
                if(item.getId() == product.getId()) {
                    product.setQuantity(String.valueOf((Integer.valueOf(product.getQuantity()) + 1)));
                    order.getProducts().set(order.getProducts().indexOf(item), product);
                    isFind = true;
                }
            }

            if(!isFind) {
                order.getProducts().add(product);
            }

            order.incrementQuantity();
            order.setTotalPrice(order.getTotalPrice() + calculPrice(product));
        }

        return product;
    }

    public Product removeProduct(Product product) {
        if(order == null) {
            return null;
        }

        Boolean isRemove = false;
        Boolean isFind = false;
        for(Product item : order.getProducts()) {
            if(item.getId() == product.getId()) {
                isFind = true;

                if(Integer.valueOf(item.getQuantity()) > 1) {
                    product.setQuantity(String.valueOf((Integer.valueOf(product.getQuantity()) - 1)));
                } else {
                    isRemove = true;
                    order.getProducts().remove(item);
                    break;
                }
            }
        }

        if(isFind) {
            order.decrementQuantity();
            order.setTotalPrice(order.getTotalPrice() - calculPrice(product));
        }

        return isRemove ? null : product;
    }

    public void removeOrder() {
        order = null;
    }

    private double calculPrice(Product product) {
        return Double.parseDouble(product.getPrice());
    }

    public String getTotalQuantityAsString() {
        return order == null ? "" : String.valueOf(order.getTotalQuantity());
    }

    public String getTotalPriceAsString() {
        return order == null ? "0" : UnitPriceUtils.addEuro(String.valueOf(order.getTotalPrice()));
    }

    public int getProductSize() {
        if(order == null) {
            return 0;
        } else if(order.getProducts() == null) {
            return 0;
        } else {
            return order.getProducts().size();
        }
    }
}
