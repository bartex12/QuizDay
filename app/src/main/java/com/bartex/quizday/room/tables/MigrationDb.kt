package com.bartex.quizday.room.tables

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bartex.quizday.room.Database

class MigrationDb {
   val migration1to2 = object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE RoomState ADD COLUMN mistake INTEGER DEFAULT 0  NOT NULL")
        }
    }

    val migration2to3 = object : Migration(2,3){
        override fun migrate(database: SupportSQLiteDatabase) {
//            val db = Database.getInstance() as Database
//            val oldFlags :List<RoomState> = db.stateDao.findByFlag("https://restcountries.eu")
//            db.stateDao.delete(oldFlags)
//          database.query("SELECT * FROM RoomState WHERE flag LIKE '%' ||:oldFlag || '%'")
//            database.delete()
        }
    }

}