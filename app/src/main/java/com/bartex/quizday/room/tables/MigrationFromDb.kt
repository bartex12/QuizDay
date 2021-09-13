package com.bartex.quizday.room.tables

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFromDb {
   val migration1to2 = object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE RoomState ADD COLUMN mistake INTEGER DEFAULT 0  NOT NULL")
        }
    }
}