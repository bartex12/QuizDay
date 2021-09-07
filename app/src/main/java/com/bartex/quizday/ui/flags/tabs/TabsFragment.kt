package com.bartex.quizday.ui.flags.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.ui.flags.tabs.flag.FlagsFragment
import com.bartex.quizday.ui.flags.tabs.flag.FlagsViewModel
import com.bartex.quizday.ui.flags.tabs.state.StatesFragment
import com.bartex.quizday.ui.flags.tabs.state.StatesViewModel
import com.google.android.material.tabs.TabLayout

class TabsFragment: Fragment() {
    companion object{
        const val TAG = "33333"
    }

    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout
    lateinit var navController: NavController

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tabs,container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "**-** TabsFragment onViewCreated  ")

        navController = Navigation.findNavController(view)

        initViews(view)

        //разрешаем показ меню во фрагменте
        setHasOptionsMenu(true)

        viewPager.adapter = ViewPageAdapter(childFragmentManager )
        tabLayout.setupWithViewPager(viewPager)

        //устанавливаем текущую вкладку
        val tabPosition = arguments?.getInt(Constants.PAGER_POSITION, 0)
        tabPosition?. let{
            viewPager.currentItem = it
        }?: let{viewPager.currentItem = 0}
        Log.d(TAG, "**-** TabsFragment onViewCreated  viewPager.currentItem = ${viewPager.currentItem}")
    }

    private fun initViews(view: View) {
        viewPager =view.findViewById(R.id.view_pager_flags)
        tabLayout =view.findViewById(R.id.tab_layout_flags)
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        FlagsFragment()
                        val flagsViewModel by lazy {
                            ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
                        }
                        flagsViewModel.resetQuiz()
                    }
                    1 -> {
                        StatesFragment()
                        val statesViewModel by lazy{
                            ViewModelProvider(requireActivity()).get(StatesViewModel::class.java)
                        }
                        statesViewModel.resetQuiz()
                    }
                }
            }
        })
    }
}