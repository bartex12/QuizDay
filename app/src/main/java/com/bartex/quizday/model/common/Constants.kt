package com.bartex.quizday.model.common

object Constants {
    const val baseUrl =  "https://restcountries.eu/rest/v2/"
    const val SOUND = "pref_cbSound"
    const val CHOICES = "pref_numberOfChoices"
    const val FLAGS_IN_QUIZ = "pref_numberOfFlags"
    const val DIALOG_FRAGMENT = "DIALOG_FRAGMENT_TAG"

    const val  REGION_EUROPE = "Европа"
    const val  REGION_ASIA = "Азия"
    const val  REGION_AMERICAS = "Америка"
    const val  REGION_AFRICA = "Африка"
    const val  REGION_OCEANIA = "Океания"
    const val  REGION_ALL= "Все"

    val  LIST_OF_REGIONS= arrayOf( REGION_ALL, REGION_EUROPE, REGION_ASIA, REGION_AMERICAS,
            REGION_AFRICA, REGION_OCEANIA)

    const val TOTAL_QUESTIONS = "TOTAL_QUESTIONS"
    const val TOTAL_GUESSES = "TOTAL_GUESSES"

    const val PAGER_POSITION = "PAGER_POSITION"
    const val  FIRST_POSITION_STATE = "FIRST_POSITION_STATE"
    const val  TOOLBAR_TITLE_TEXT = "TOOLBAR_TITLE_TEXT"
}