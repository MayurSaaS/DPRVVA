package com.vvautotest.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vvautotest.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(Category Category);
    @Query("Select * from image_categories")
    List<Category> getCategoryList();
    @Query("SELECT * FROM image_categories WHERE name = :name")
    List<Category> getCategoryByName(String name);
    @Query("SELECT EXISTS(SELECT * FROM image_categories WHERE id = :id)")
    boolean isCategoryExist(int id);
    @Update
    void updateCategory(Category Category);
    @Delete
    void deleteCategory(Category Category);
    @Query("DELETE FROM image_categories WHERE name = :name")
    void deleteCategoryByPhone(String name);
}
