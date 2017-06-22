package com.sao.mobile.sao.manager;

import android.content.Context;
import android.content.Intent;

import com.sao.mobile.sao.ui.activity.OrderActivity;
import com.sao.mobile.saolib.entities.Order;
import com.sao.mobile.saolib.entities.Product;
import com.sao.mobile.saolib.utils.UnitPriceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seb on 25/01/2017.
 */
public class OrderManager {
    private static OrderManager ourInstance = new OrderManager();
    public Order order;
    private UserManager mUserManager = UserManager.getInstance();

    private OrderManager() {
    }

    public static OrderManager getInstance() {
        return ourInstance;
    }

    public Product addProduct(Product product) {
        if(mUserManager.currentBar == null) {
            return null;
        }

        if(order == null) {
            List<Product> products = new ArrayList<>();
            product.setQuantity(1);
            products.add(product);
            order = new Order(mUserManager.getFacebookUserId(), mUserManager.currentBar.getBarId(), calculPrice(product), products);
        } else {
            Boolean isFind = false;
            for(Product item : order.getProducts()) {
                if(item.getProductId().equals(product.getProductId())) {
                    product.setQuantity(product.getQuantity() + 1);
                    order.getProducts().set(order.getProducts().indexOf(item), product);
                    isFind = true;
                }
            }

            if(!isFind) {
                product.setQuantity(1);
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
            if(item.getProductId().equals(product.getProductId())) {
                isFind = true;

                if(Integer.valueOf(item.getQuantity()) > 1) {
                    product.setQuantity(product.getQuantity() - 1);
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
        if (mUserManager.currentBar != null && order != null && mUserManager.currentBar.getBarId().equals(order.getBarId())) {
            return;
        }

        order = null;
    }

    private double calculPrice(Product product) {
        return Double.parseDouble(product.getPrice().toString());
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

    public void setReadyOrder() {
        if(!isOrderOk()) {
            return;
        }

        order.setStep(Order.Step.READY);
    }

    public void setInprogressOrder() {
        if(!isOrderOk()) {
            return;
        }

        order.setStep(Order.Step.INPROGRESS);
    }

    public void setNewOrder() {
        if(!isOrderOk()) {
            return;
        }

        order.setStep(Order.Step.NEW);
    }

    public Boolean isOrderOk() {
        return order != null;
    }

    public Boolean isProductOk() {
        return order != null && order.getProducts() != null && order.getProducts().size() > 0;
    }

    public void startOrderActivity(Context context) {
        if(!isProductOk()) {
            return;
        }

        Class clazz = order.getStep().equals(Order.Step.NEW) ? OrderActivity.class : OrderActivity.class;
        context.startActivity(new Intent(context, clazz));
    }
}
