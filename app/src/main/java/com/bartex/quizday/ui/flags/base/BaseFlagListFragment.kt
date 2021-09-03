package com.bartex.quizday.ui.flags.base

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bartex.quizday.ui.flags.mistakes.MistakesViewModel
import com.bartex.quizday.ui.flags.regions.RegionViewModel

abstract class BaseFlagListFragment: Fragment(),
        SearchView.OnQueryTextListener {

   val baseViewModel by lazy {
        ViewModelProvider(requireActivity()).get(BaseViewModel::class.java)
   }


    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }
}