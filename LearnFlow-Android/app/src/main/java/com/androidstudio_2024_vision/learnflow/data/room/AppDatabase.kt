package com.androidstudio_2024_vision.learnflow.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
//数据库的搭建
@Database(
    entities = [
        NoteEntity::class,
        HistoryEntity::class,
        TaskEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase :
    RoomDatabase() {

    abstract fun noteDao():
            NoteDao

    abstract fun historyDao():
            HistoryDao

    abstract fun taskDao():
            TaskDao

    companion object {

        @Volatile
        private var INSTANCE:
                AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    val instance =
                        Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            "learnflow_db"
                        ).fallbackToDestructiveMigration().build()

                    INSTANCE =
                        instance

                    instance
                }
        }
    }
}