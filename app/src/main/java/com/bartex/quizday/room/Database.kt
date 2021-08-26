package com.bartex.quizday.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bartex.quizday.room.dao.StateDao
import com.bartex.quizday.room.tables.RoomState

@androidx.room.Database(entities = [RoomState::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract val stateDao:StateDao

    companion object{
        const val DB_NAME = "database.db"

       private var instance:Database? = null

        //получение экземпляра базы
       fun  getInstance() = instance?:RuntimeException("База данных не создана")

        //создание базы - вызов в App
        fun create(context:Context) {
           if(instance == null) {
               instance = Room.databaseBuilder(context, Database::class.java, DB_NAME )
                   .build()
           }
        }
    }
}