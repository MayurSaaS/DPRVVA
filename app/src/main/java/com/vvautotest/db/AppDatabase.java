package com.vvautotest.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vvautotest.model.Category;
import com.vvautotest.model.OfflinePhotoModel;
import com.vvautotest.model.UserItem;

@Database(entities = {Category.class, UserItem.class, OfflinePhotoModel.class},exportSchema = false, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();
    public abstract UserItemDao userItemDao();
    public abstract PhotoDao  photoDao();
    private static AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context)
    {
        if(INSTANCE == null)
        {
            synchronized (AppDatabase.class)
            {
                if(INSTANCE == null) {
                    INSTANCE =        Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "vvautotest_database").fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

}