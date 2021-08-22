package com.bartex.quizday.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bartex.quizday.R

class HelpFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?) =
        View.inflate(context, R.layout.fragment_help, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
        requireActivity().invalidateOptionsMenu()
    }
}