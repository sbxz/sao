package com.sao.mobile.sao.entities;

/**
 * Created by Seb on 15/02/2017.
 */

public class ActuBar {
    private Bar Bar;
    private String date;
    private String text;

    public ActuBar(com.sao.mobile.sao.entities.Bar bar, String date, String text) {
        Bar = bar;
        this.date = date;
        this.text = text;
    }

    public com.sao.mobile.sao.entities.Bar getBar() {
        return Bar;
    }

    public void setBar(com.sao.mobile.sao.entities.Bar bar) {
        Bar = bar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
