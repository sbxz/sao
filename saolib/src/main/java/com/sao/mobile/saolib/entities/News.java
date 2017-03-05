package com.sao.mobile.saolib.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Seb on 15/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class News implements Serializable{
    @SerializedName("newsId")
    private Long newsId;

    @SerializedName("bar")
    private Bar bar;

    @SerializedName("created")
    private long created;

    @SerializedName("content")
    private String content;

    public News() {
    }

    public News(Bar bar, Long created, String content) {
        this.bar = bar;
        this.created = created;
        this.content = content;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
