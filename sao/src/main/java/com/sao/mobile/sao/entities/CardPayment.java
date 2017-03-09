package com.sao.mobile.sao.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seb on 09/03/2017.
 */

public class CardPayment implements Serializable {
    private String name;
    private String number;
    private String date;
    private String cvc;

    public CardPayment() {
    }

    public CardPayment(String name, String number, String date, String cvc) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.cvc = cvc;
    }

    public CardPayment(List<String> cardList) {
        for (String item : cardList) {
            if (isNumeric(item.replaceAll(" ", ""))) {
                if (item.length() == 19) {
                    this.number = item;
                }
            } else if (isNumeric(item.replaceAll("/", ""))) {
                if (item.length() == 5) {
                    this.date = item;
                }
            } else if (isNumeric(item)) {
                if (item.length() == 3) {
                    this.cvc = item;
                }
            } else {
                this.name = item;
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
