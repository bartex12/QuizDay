package com.bartex.quizday.model.repositories.help

import com.bartex.quizday.App
import com.bartex.quizday.R

class HelpRepo(val app: App): IHelpRepo {

    override fun getHelpText(): String {
        return app.applicationContext.getString(R.string.helpString222)
    }

}