package com.arc.arc.core.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Native Room Database for ARC offline-first architecture.
 * Schema version 1, exportSchema = true.
 */
@Database(
    entities = {TaskEntity.class},
    version = 1,
    exportSchema = true
)
public abstract class ArcDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "arc_native.db";

    public abstract TaskDao taskDao();

    private static volatile ArcDatabase INSTANCE;

    public static ArcDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ArcDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        ArcDatabase.class,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration()
                     .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ArcDatabase createInMemory(Context context) {
        return Room.inMemoryDatabaseBuilder(
            context.getApplicationContext(),
            ArcDatabase.class
        ).allowMainThreadQueries()
         .build();
    }
}
