package com.hudou.autotest.database.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hudou.autotest.database.dao.ResultDao;
import com.hudou.autotest.database.entity.ResultDataEntity;
import com.hudou.autotest.database.entity.ResultItemEntity;

@Database(entities = {ResultItemEntity.class, ResultDataEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ResultDao dao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "test_results.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
