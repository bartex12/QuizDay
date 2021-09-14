package com.bartex.quizday.ui.flags.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bartex.quizday.R
import com.bartex.quizday.ui.flags.tabs.regions.RegionFragment
import com.bartex.quizday.ui.flags.tabs.flag.FlagsFragment
import com.bartex.quizday.ui.flags.tabs.mistakes.MistakesFragment
import com.bartex.quizday.ui.flags.tabs.state.StatesFragment

class ViewPageAdapter(val context:Context, fragmentManager : FragmentManager)
    :FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = arrayOf(
            FlagsFragment(),
            StatesFragment(),
            MistakesFragment(),
            RegionFragment()
    )

    private val titles = arrayOf(
            context.getString(R.string.flags),
            context.getString(R.string.states),
            context.getString(R.string.mistakes),
            context.getString(R.string.regions)
    )

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
      return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return  titles[position]
    }
}