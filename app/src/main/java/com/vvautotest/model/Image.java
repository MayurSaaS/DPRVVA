package com.vvautotest.model;

import java.io.Serializable;

public class Image implements Serializable {
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
