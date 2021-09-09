package com.bartex.quizday.ui.flags.tabs.regions

import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

object UtilRegions {

    fun getRegionId(region: String): Int {
        return when (region) {
            Constants.REGION_ALL -> R.id.chip_all_region
            Constants.REGION_EUROPE -> R.id.chip_Europa_region
            Constants.REGION_ASIA -> R.id.chip_Asia_region
            Constants.REGION_AMERICAS -> R.id.chip_America_region
            Constants.REGION_OCEANIA -> R.id.chip_Oceania_region
            Constants.REGION_AFRICA -> R.id.chip_Africa_region
            else -> R.id.chip_Europa_region
        }
    }


    private fun getRegionName(id: Int): String {
        return when (id) {
            R.id.chip_all_region -> Constants.REGION_ALL
            R.id.chip_Europa_region -> Constants.REGION_EUROPE
            R.id.chip_Asia_region -> Constants.REGION_ASIA
            R.id.chip_America_region -> Constants.REGION_AMERICAS
            R.id.chip_Oceania_region -> Constants.REGION_OCEANIA
            R.id.chip_Africa_region -> Constants.REGION_AFRICA
            else -> Constants.REGION_EUROPE
        }
    }

    //показываем количество ошибок по регионам
    fun showCountByRegion(chipGroupMistake: ChipGroup, listOfMistakeStates: MutableList<State>){
        var regionNameAndCount = ""
        for (i in 0 until chipGroupMistake.childCount) {
            val chip = chipGroupMistake.getChildAt(i) as Chip
            regionNameAndCount =  when(chip.id){
                R.id.chip_all_region -> "${getRegionName(chip.id)} ${listOfMistakeStates.size}"
                R.id.chip_Europa_region-> {
                    getChipText(listOfMistakeStates, Constants.REGION_EUROPE,  chip.id)
                }
                R.id.chip_Asia_region-> {
                    getChipText(listOfMistakeStates, Constants.REGION_ASIA,  chip.id)
                }
                R.id.chip_America_region-> {
                    getChipText(listOfMistakeStates, Constants.REGION_AMERICAS,  chip.id)
                }
                R.id.chip_Oceania_region-> {
                    getChipText(listOfMistakeStates, Constants.REGION_OCEANIA,  chip.id)
                }
                R.id.chip_Africa_region->{
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
    private fun getChipText(listOfMistakeStates: MutableList<State>, regionRus:String, chipId:Int): String {
        val filteredList = listOfMistakeStates.filter { state ->
            state.regionRus == regionRus
        } as MutableList<State>
        return "${getRegionName(chipId)} ${filteredList.size}"
    }
}