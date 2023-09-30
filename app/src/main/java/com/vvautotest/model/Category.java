package com.vvautotest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_categories")
public class Category {

      @PrimaryKey(autoGenerate = true)
      public int id;
      @ColumnInfo(name = "name")
      public String name;
      @ColumnInfo(name = "sort_Order")
      public int sortOrder;

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

      public int getSortOrder() {
            return sortOrder;
      }

      public void setSortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
      }
}
