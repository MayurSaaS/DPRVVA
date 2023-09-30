package com.vvautotest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "users")
public class UserItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "loginID")
    public String loginID;
    @ColumnInfo(name = "password")
    public String password;
    @ColumnInfo(name = "userTypeID")
    public int userTypeID;
    @ColumnInfo(name = "employeeID")
    public int employeeID;
    @ColumnInfo(name = "deviceID")
    public String deviceID;
    @ColumnInfo(name = "isActive")
    public boolean isActive;
    @ColumnInfo(name = "createdByUserID")
    public int createdByUserID;
    @ColumnInfo(name = "createdByDateTime")
    public String createdByDateTime;
}
