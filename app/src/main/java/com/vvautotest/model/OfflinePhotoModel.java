package com.vvautotest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offline_photo")
public class OfflinePhotoModel {
    @PrimaryKey(autoGenerate = true)
    long id;
    @ColumnInfo(name = "category_name")
    String category_name;
    @ColumnInfo(name = "category_id")
    String category_id;
    @ColumnInfo(name = "category_id")
    String description;
    @ColumnInfo(name = "image_extension")
    String image_extension;
    @ColumnInfo(name = "user_id")
    String user_id;
    @ColumnInfo(name = "image")
    String image;
    @ColumnInfo(name = "image_name")
    String image_name;
    @ColumnInfo(name = "is_uploaded")
    boolean is_uploaded;
    @ColumnInfo(name = "site_id")
    int site_id;
    @ColumnInfo(name = "created_date")
    String created_date;


    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public boolean isIs_uploaded() {
        return is_uploaded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_extension() {
        return image_extension;
    }

    public void setImage_extension(String image_extension) {
        this.image_extension = image_extension;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setIs_uploaded(boolean is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
