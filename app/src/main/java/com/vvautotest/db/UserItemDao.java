package com.vvautotest.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vvautotest.model.UserItem;

import java.util.List;

@Dao
public interface UserItemDao {
    @Insert
    void insertUser(UserItem Category);
    @Query("Select * from users")
    List<UserItem> getUsersList();
    @Query("SELECT * FROM users WHERE name = :name")
    List<UserItem> getUserByName(String name);
    @Query("SELECT EXISTS(SELECT * FROM users WHERE id = :id)")
    boolean isUserExist(int id);
    @Update
    void updateUser(UserItem userItem);
    @Delete
    void deleteUser(UserItem Category);
    @Query("DELETE FROM users WHERE name = :name")
    void deleteUserByPhone(String name);
}
