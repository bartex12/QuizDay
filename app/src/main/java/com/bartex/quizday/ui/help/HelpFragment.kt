package com.bartex.quizday.ui.help

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment: Fragment() {

    companion object {
        const val QUIZ: String ="Quizday"
        const val TEXT_QUIZ: String ="текстовые"
        const val GRAFIC_QUIZ: String ="графические"
        const val FLAGS: String ="Флаги"
        const val STATES: String = "Страны"
        const val MISTAKES: String = "Ошибки"
        const val REGIONS: String = "Регионы"
        const val SEARCH: String = "Функция поиска"
        const val SETTINGS: String = "настройки"
    }

    private lateinit var helpViewModel: HelpViewModel
    lateinit var navController: NavController

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        helpViewModel = ViewModelProvider(this).get(HelpViewModel::class.java)

        val helpText = helpViewModel.getHelpText()
        val spanHelp = SpannableString(helpText)
        val color = requireActivity().resources.getColor(R.color.purple_500)

        helpText.let {
            setStyle(it, QUIZ, spanHelp)
          //  makeLinks(it, TEXT_QUIZ, color, spanHelp)
            makeLinks(it, GRAFIC_QUIZ, color, spanHelp)
            makeLinks(it, FLAGS, color, spanHelp)
            makeLinks(it, STATES, color, spanHelp)
            makeLinks(it, MISTAKES, color, spanHelp)
            makeLinks(it, REGIONS, color, spanHelp)
            makeLinks(it, SEARCH, color, spanHelp)
            makeLinks(it, SETTINGS, color, spanHelp)

            //Чтобы TextView корректно обрабатывал клик на подстроке, нужно настроить параметр
            // movementMethod. Он указывает, кому делегировать touch event. В нашем случае
            // мы просим TextView делегировать клик в LinkMovementMethod, который ищет
            // ClickableSpan и вызывает на них onClick.
            tv_help.movementMethod = LinkMovementMethod.getInstance()
            tv_help.setText(spanHelp, TextView.BufferType.SPANNABLE)

            //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
            //без этой строки меню в тулбаре ведёт себя неправильно
            setHasOptionsMenu(true)
            requireActivity().invalidateOptionsMenu()
        }
    }
    private fun setStyle(text:String, phrase:String, spanHelp: Spannable){
        val start = text.indexOf(phrase)
        val end = start + phrase.length

        spanHelp.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun makeLinks(text: String, phrase: String, phraseColor:Int, spanHelp: Spannable){

        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                ds.color = phraseColor // устанавливаем наш цвет
                ds.isUnderlineText = true // нижнее подчеркивание
            }

            override fun onClick(view: View) {
                val bundle = Bundle()
                when(phrase){
                   // TEXT_QUIZ -> navController.navigate(R.id.textquizFragment)
                    GRAFIC_QUIZ -> navController.navigate(R.id.tabsFragment)
                    FLAGS-> goTo(bundle, 0)
                    STATES-> goTo(bundle, 1)
                    MISTAKES-> goTo(bundle, 2)
                    REGIONS,SEARCH -> goTo(bundle, 3)
                    SETTINGS-> navController.navigate(R.id.settingsFragment)
                    else -> {
                        goTo(bundle, 0)
                    }
                }
            }
        }
        val start = text.indexOf(phrase)
        val end = start + phrase.length
        spanHelp.setSpan(
                clickableSpan,
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE )
    }

    private fun goTo(bundle: Bundle, position:Int) {
        bundle.putInt(Constants.PAGER_POSITION, position)
        navController.navigate(R.id.action_helpFragment_to_tabsFragment, bundle)
    }

}