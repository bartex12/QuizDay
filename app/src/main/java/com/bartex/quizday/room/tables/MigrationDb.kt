package com.bartex.quizday.room.tables


import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationDb {
    companion object{
        const val TAG = "Quizday"
    }

   val migration1to2 = object : Migration(1, 2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE RoomState ADD COLUMN mistake INTEGER DEFAULT 0  NOT NULL")
        }
    }

    val migration2to3 = object : Migration(2, 3){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.delete("RoomState", "flag=?", arrayOf("https://restcountries.eu/data/ala.svg"))
        }
    }

}