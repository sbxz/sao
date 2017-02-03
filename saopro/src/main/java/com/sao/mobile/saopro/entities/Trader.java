package com.sao.mobile.saopro.entities;

import java.io.Serializable;

/**
 * Created by Seb on 30/11/2016.
 */

public class Trader implements Serializable{
    private Bar currentBar;

    public Bar getCurrentBar() {
        return currentBar;
    }

    public void setCurrentBar(Bar currentBar) {
        this.currentBar = currentBar;
    }
}
