package com.example.trialproject3.Domain;

import java.io.Serializable;

public class CategoryDomain implements Serializable {
    private String title;

    private String picUrl;


    public CategoryDomain(String title, String picUrl) {
        this.title = title;
        this.picUrl = picUrl;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
