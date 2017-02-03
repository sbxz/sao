package com.sao.mobile.saopro.entities.api;

import com.google.gson.annotations.SerializedName;
import com.sao.mobile.saopro.entities.Bar;

import java.util.List;

/**
 * Created by Seb on 29/11/2016.
 */

public class BarResponse {
    @SerializedName("results")
    private List<Bar> results;
    @SerializedName("totalResults")
    private int totalResults;

    public List<Bar> getResults() {
        return results;
    }

    public void setResults(List<Bar> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
