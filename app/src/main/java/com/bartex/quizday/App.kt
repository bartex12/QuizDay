package com.bartex.quizday

import android.app.Application
import com.bartex.quizday.room.Database

class App: Application() {

    companion object{
        lateinit var instance:App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //создаём базу данных
        Database.create(this)
    }
}