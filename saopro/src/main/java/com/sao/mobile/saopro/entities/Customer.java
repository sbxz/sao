package com.sao.mobile.saopro.entities;

import java.io.Serializable;

/**
 * Created by Seb on 31/12/2016.
 */

public class Customer implements Serializable {
    private String firstName;
    private String name;
    private String pseudo;
    private String thumbnail;

    public Customer(String firstName, String name, String pseudo, String thumbnail) {
        this.firstName = firstName;
        this.name = name;
        this.pseudo = pseudo;
        this.thumbnail = thumbnail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public static String getCustomerName(Customer customer) {
        String name = "Client";

        if(customer == null) {
            return name;
        }

        if(customer.getPseudo() != null) {
            name = customer.getPseudo();
        } else if(customer.getFirstName() != null && customer.getName() != null) {
            name = customer.getFirstName() + " " + customer.getName();
        } else if (customer.getName() != null) {
            name = customer.getName();
        } else if (customer.getFirstName() != null) {
            name = customer.getFirstName();
        }

        return name;
    }
}
