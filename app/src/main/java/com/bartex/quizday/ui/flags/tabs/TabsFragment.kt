package com.bartex.quizday.ui.flags.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.google.android.material.tabs.TabLayout

class TabsFragment: Fragment() {

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

        navController = Navigation.findNavController(view)

        initViews(view)

        //разрешаем показ меню во фрагменте
        setHasOptionsMenu(true)

        viewPager.adapter = ViewPageAdapter(requireActivity(),  childFragmentManager )
        tabLayout.setupWithViewPager(viewPager)

        //устанавливаем текущую вкладку
        val tabPosition = arguments?.getInt(Constants.PAGER_POSITION, 0)
        tabPosition?. let{
            viewPager.currentItem = it
        }?: let{viewPager.currentItem = 0}
    }

    private fun initViews(view: View) {
        viewPager =view.findViewById(R.id.view_pager_flags)
        tabLayout =view.findViewById(R.id.tab_layout_flags)
    }
}