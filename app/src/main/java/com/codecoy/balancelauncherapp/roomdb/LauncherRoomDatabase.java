package com.codecoy.balancelauncherapp.roomdb;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.codecoy.balancelauncherapp.roomdb.entities.AppsDataEntity;
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedAppsEntity;
import com.codecoy.balancelauncherapp.roomdb.entities.BlockedWebsiteEntity;
import com.codecoy.balancelauncherapp.roomdb.entities.BlocksCatsEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { BlockedAppsEntity.class, BlocksCatsEntity.class, BlockedWebsiteEntity.class, AppsDataEntity.class}, version = 1, exportSchema = false)
public abstract class LauncherRoomDatabase extends RoomDatabase {
    public abstract LauncherDao db_dao();

    private static volatile LauncherRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static LauncherRoomDatabase getDatabase(Context applicationContext) {
        if (INSTANCE == null) {
            synchronized (LauncherRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(applicationContext,
                            LauncherRoomDatabase.class, "LauncherDB")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
