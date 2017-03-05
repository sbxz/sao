package com.sao.mobile.sao.manager;

import com.sao.mobile.saolib.entities.Bar;
import com.sao.mobile.saolib.entities.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Seb on 04/03/2017.
 */
public class BarProductManager {
    private static BarProductManager ourInstance = new BarProductManager();

    public static BarProductManager getInstance() {
        return ourInstance;
    }

    public Map<String, Map<String, List<Product>>> barCatalog;

    private BarProductManager() {
        barCatalog = new HashMap<>();
    }

    public void addBarCatalog(Bar bar, Map<String, List<Product>> catalog) {
        if(barCatalog.containsKey(bar.getName())) {
            return;
        }

        barCatalog.put(bar.getName(), catalog);
    }

    public Map<String, List<Product>> getBarCatalog(Bar bar) {
        return barCatalog.get(bar.getName());
    }
}
