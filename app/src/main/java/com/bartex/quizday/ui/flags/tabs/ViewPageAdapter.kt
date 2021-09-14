package com.bartex.quizday.ui.flags.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bartex.quizday.ui.flags.mistakes.MistakesFragment
import com.bartex.quizday.ui.flags.tabs.flag.FlagsFragment
import com.bartex.quizday.ui.flags.tabs.state.StatesFragment

class ViewPageAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        const val FLAGS = 0
        const val STATES = 1
        const val MISTAKES = 2
        const val REGIONS = 3
    }

    private val fragments = arrayOf(
        FlagsFragment(),
        StatesFragment(),
        MistakesFragment()/*,
            RegionFragment(),*/
    )

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> fragments[FLAGS]
            1 -> fragments[STATES]
            2 -> fragments[MISTAKES]
            3 -> fragments[REGIONS]
            else -> fragments[FLAGS]
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Флаги"
            1 -> "Страны"
            2 -> "Ошибки"
            3 -> "Таблицы"
            else -> "Флаги"
        }
    }
}