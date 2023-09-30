package com.vvautotest.model;

import android.graphics.drawable.Drawable;

public class MenuData {

    int id;
    public String name;
    public String subName;
    public String detailName;
    public String menuIcon;
    public String subMenuIcon;
    public boolean hasChildren, isGroup, isSelected;

    public MenuData(int id, String name, String subName,
                    String detailName, boolean hasChildren, boolean isGroup,
                    boolean isSelected, String menuIcon, String subMenuIcon) {
        this.id = id;
        this.name = name;
        this.subName = subName;
        this.detailName = detailName;
        this.hasChildren = hasChildren;
        this.isGroup = isGroup;
        this.isSelected = isSelected;
        this.menuIcon = menuIcon;
        this.subMenuIcon = subMenuIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getSubMenuIcon() {
        return subMenuIcon;
    }

    public void setSubMenuIcon(String subMenuIcon) {
        this.subMenuIcon = subMenuIcon;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
