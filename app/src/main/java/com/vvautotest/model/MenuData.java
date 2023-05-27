package com.vvautotest.model;

import android.graphics.drawable.Drawable;

public class MenuData {

    String title;
    Drawable image;
    int action;
    String subTitle;

    public MenuData(String title, Drawable image, int action, String subTitle) {
        this.title = title;
        this.image = image;
        this.action = action;
        this.subTitle = subTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
