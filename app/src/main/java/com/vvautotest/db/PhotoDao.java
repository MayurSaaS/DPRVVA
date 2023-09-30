package com.vvautotest.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vvautotest.model.OfflinePhotoModel;

import java.util.List;

@Dao
public interface PhotoDao {
    @Insert
    void insertPhoto(OfflinePhotoModel photoItem);
    @Query("Select * from offline_photo")
    List<OfflinePhotoModel> getPhotosList();
    @Query("SELECT * FROM offline_photo WHERE category_name = :name")
    List<OfflinePhotoModel> getPhotosByName(String name);
    @Query("SELECT EXISTS(SELECT * FROM offline_photo WHERE id = :id)")
    boolean isPhotoExist(int id);
    @Update
    void updatePhotos (OfflinePhotoModel userItem);
    @Delete
    void deletePhoto(OfflinePhotoModel Category);
    @Query("DELETE FROM offline_photo WHERE category_name = :name")
    void deletePhotoByPhone(String name);
}
