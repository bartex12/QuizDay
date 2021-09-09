package com.bartex.quizday.ui.flags.tabs.mistakes

import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

object UtilMistakes {

    fun getRegionId(region: String): Int {
        return when (region) {
            Constants.REGION_ALL -> R.id.chip_all_mistakes
            Constants.REGION_EUROPE -> R.id.chip_Europa_mistakes
            Constants.REGION_ASIA -> R.id.chip_Asia_mistakes
            Constants.REGION_AMERICAS -> R.id.chip_America_mistakes
            Constants.REGION_OCEANIA -> R.id.chip_Oceania_mistakes
            Constants.REGION_AFRICA -> R.id.chip_Africa_mistakes
            else -> R.id.chip_Europa_region
        }
    }

    fun getRegionName(id: Int): String {
        return when (id) {
            R.id.chip_all_mistakes -> Constants.REGION_ALL
            R.id.chip_Europa_mistakes -> Constants.REGION_EUROPE
            R.id.chip_Asia_mistakes -> Constants.REGION_ASIA
            R.id.chip_America_mistakes -> Constants.REGION_AMERICAS
            R.id.chip_Oceania_mistakes -> Constants.REGION_OCEANIA
            R.id.chip_Africa_mistakes -> Constants.REGION_AFRICA
            else -> Constants.REGION_EUROPE
        }
    }

    //показываем количество ошибок по регионам
    fun showCountByRegion(chipGroupMistake: ChipGroup, listOfMistakeStates: MutableList<State>){
        var regionNameAndCount = ""
        for (i in 0 until chipGroupMistake.childCount) {
            val chip = chipGroupMistake.getChildAt(i) as Chip
            regionNameAndCount =  when(chip.id){
                R.id.chip_all_mistakes -> "${getRegionName(chip.id)} ${listOfMistakeStates.size}"
                R.id.chip_Europa_mistakes-> {
                    getChipText(listOfMistakeStates, Constants.REGION_EUROPE,  chip.id)
                }
                R.id.chip_Asia_mistakes-> {
                    getChipText(listOfMistakeStates, Constants.REGION_ASIA,  chip.id)
                }
                R.id.chip_America_mistakes-> {
                    getChipText(listOfMistakeStates, Constants.REGION_AMERICAS,  chip.id)
                }
                R.id.chip_Oceania_mistakes-> {
                    getChipText(listOfMistakeStates, Constants.REGION_OCEANIA,  chip.id)
                }
                R.id.chip_Africa_mistakes->{
                    getChipText(listOfMistakeStates, Constants.REGION_AFRICA,  chip.id)
                }
                else ->{
                    getChipText(listOfMistakeStates, Constants.REGION_EUROPE,  chip.id)
                }
            }
            chip.text = regionNameAndCount
        }
    }

    //текст на чипе региона
    private fun getChipText(listOfMistakeStates: MutableList<State>, regionRus:String,  chipId:Int): String {
        val filteredList = listOfMistakeStates.filter { state ->
            state.regionRus == regionRus
        } as MutableList<State>
        return "${getRegionName(chipId)} ${filteredList.size}"
    }
}