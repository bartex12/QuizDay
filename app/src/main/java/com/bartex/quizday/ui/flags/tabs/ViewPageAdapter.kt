package com.bartex.quizday.ui.flags.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bartex.quizday.ui.flags.FlagsFragment
import com.bartex.quizday.ui.flags.mistakes.MistakesFragment
import com.bartex.quizday.ui.flags.regions.RegionFragment

class ViewPageAdapter(fragmentManager : FragmentManager)
    :FragmentPagerAdapter(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val FLAGS = 0
        const val MISTAKES = 1
        const val STATES = 2
    }

    private val fragments = arrayOf(
            FlagsFragment(),
            MistakesFragment(),
            RegionFragment(),
    )

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
      return  when (position) {
            0 -> fragments[FLAGS]
            1 -> fragments[MISTAKES]
            2 -> fragments[STATES]
            else -> fragments[FLAGS]
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return  when(position){
            0->"Флаги"
            1->"Ошибки"
            2->"Страны"
            else -> "Флаги"
        }
    }
}