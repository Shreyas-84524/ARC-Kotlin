package com.arc.arc.core.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Native Room Database for ARC offline-first architecture.
 * Schema version 2, exportSchema = true.
 */
@Database(
    entities = {TaskEntity.class, HabitEntity.class, HabitEntryEntity.class},
    version = 2,
    exportSchema = true
)
public abstract class ArcDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "arc_native.db";

    public abstract TaskDao taskDao();
    public abstract HabitDao habitDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `habits` (" +
                "`id` TEXT NOT NULL, " +
                "`ownerUid` TEXT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`category` TEXT NOT NULL, " +
                "`colorValue` INTEGER NOT NULL, " +
                "`reminderTime` TEXT, " +
                "`createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`))");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habits_ownerUid` ON `habits` (`ownerUid`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habits_ownerUid_name` ON `habits` (`ownerUid`, `name`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habits_ownerUid_createdAt` ON `habits` (`ownerUid`, `createdAt`)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `habit_entries` (" +
                "`id` TEXT NOT NULL, " +
                "`ownerUid` TEXT NOT NULL, " +
                "`habitId` TEXT NOT NULL, " +
                "`dateStr` TEXT NOT NULL, " +
                "`completed` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`), " +
                "FOREIGN KEY(`habitId`) REFERENCES `habits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_entries_ownerUid` ON `habit_entries` (`ownerUid`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_entries_habitId` ON `habit_entries` (`habitId`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_entries_ownerUid_dateStr` ON `habit_entries` (`ownerUid`, `dateStr`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_habit_entries_ownerUid_habitId_dateStr` ON `habit_entries` (`ownerUid`, `habitId`, `dateStr`)");
        }
    };

    private static volatile ArcDatabase INSTANCE;

    public static ArcDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ArcDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        ArcDatabase.class,
                        DATABASE_NAME
                    ).addMigrations(MIGRATION_1_2)
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
