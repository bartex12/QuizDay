package com.bartex.quizday.ui.flags.tabs.mistakes

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.ui.adapters.MistakesAdapter
import com.bartex.quizday.ui.adapters.SvgImageLoader
import com.bartex.quizday.ui.flags.shared.SharedViewModel
import com.bartex.quizday.ui.flags.utils.UtilFilters
import com.bartex.quizday.ui.flags.utils.UtilMistakes
import com.google.android.material.chip.ChipGroup
import java.util.*

class MistakesFragment: Fragment(),
        SearchView.OnQueryTextListener {

    private var position = 0
    private var adapter:  MistakesAdapter? = null
    private lateinit var navController: NavController

    private val mistakesViewModel by lazy{
        ViewModelProvider(requireActivity()).get(MistakesViewModel::class.java)
    }

    private val model: SharedViewModel by activityViewModels()

    private var listOfMistakeStates  = mutableListOf<State>() //список стран региона с ошибками
    private lateinit var rvStatesMistake: RecyclerView
    private lateinit  var emptyViewMistake: TextView
    private lateinit var chipGroupMistake: ChipGroup
    private var region:String = Constants.REGION_ALL

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_mistakes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        //восстанавливаем позицию списка после поворота или возвращения на экран
        position =  mistakesViewModel.getPositionState()

        initViews(view)
        initAdapter()
        initChipGroupListener()
        initMenu()

        //чтобы получить текущий регион - сделал обмен данными через SharedViewModel
        // во FlagsFragment и StateFragment в initChipGroupListener() кладём значение, а здесь принимаем
        model.newRegion.observe(viewLifecycleOwner,{newRegion->
            region = newRegion
            chipGroupMistake.check(UtilMistakes.getRegionId(region)) //отметка на чипе
            //не убирать эту строку иначе при повороте данные пропадают!
            renderDataWithRegion(region)
        })

        //получаем все ошибки автоматически при любом изменении в базе данных
        mistakesViewModel.getAllMistakesLive()
                .observe(viewLifecycleOwner, {
                    listOfMistakeStates =   it.map {room->
                        State(capital =room.capital, flags = listOf(room.flag), name =room.name,
                            continent = room.region, nameRus = room.nameRus,
                            capitalRus = room.capitalRus, regionRus = room.regionRus
                        )
                    }.filter {st-> //отбираем только те, где полные данные
                        UtilFilters.filterData(st)
                    }  as MutableList<State>

                    UtilMistakes.showCountByRegion(chipGroupMistake, listOfMistakeStates)
                    chipGroupMistake.check(UtilMistakes.getRegionId(region))//отметка на чипе
                    renderDataWithRegion(region)
                })
    }

    //запоминаем  позицию списка, на которой сделан клик - на случай поворота экрана
    override fun onPause() {
        super.onPause()
        //определяем первую видимую позицию
        val manager = rvStatesMistake.layoutManager as LinearLayoutManager
        val firstPosition = manager.findFirstVisibleItemPosition()
        mistakesViewModel.savePositionState(firstPosition)
    }

    private fun initMenu() {
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private fun initViews(view: View) {
        rvStatesMistake = view.findViewById(R.id.rv_states_mistakes)
        emptyViewMistake = view.findViewById(R.id.empty_view_mistakes)
        chipGroupMistake = view.findViewById(R.id.chip_region_mistakes)
    }

    private fun initAdapter() {
        rvStatesMistake.layoutManager = LinearLayoutManager(requireActivity())

        adapter = MistakesAdapter(
                getOnRemoveListener(),
                SvgImageLoader(requireActivity())
        )
        rvStatesMistake.adapter = adapter
    }

    private fun renderData(listOfMistakeStates:MutableList<State>) {
        if(listOfMistakeStates.isEmpty()){
            rvStatesMistake.visibility = View.GONE
            emptyViewMistake.visibility = View.VISIBLE
            if (region == Constants.REGION_ALL){
                emptyViewMistake.text = getString(R.string.no_data_state_test_all)
            }else{
                emptyViewMistake.text = getString(R.string.no_data_state_test_region)
            }
        }else{
            rvStatesMistake.visibility =  View.VISIBLE
            emptyViewMistake.visibility = View.GONE

            listOfMistakeStates.sortBy {it.nameRus}
            adapter?.listOfMistakes = listOfMistakeStates

            rvStatesMistake.layoutManager?.scrollToPosition(position) //крутим в запомненную позицию списка
        }
    }

    private fun renderDataWithRegion(newRegion: String) {
        when (newRegion) {
            Constants.REGION_ALL -> {
                renderData(listOfMistakeStates)
            }
            else -> {
                val filteredList = listOfMistakeStates.filter { state ->
                    state.regionRus == newRegion
                } as MutableList<State>
                renderData(filteredList)
            }
        }
    }

    private fun initChipGroupListener() {
        chipGroupMistake.setOnCheckedChangeListener { _, id ->
            region = UtilMistakes.getRegionName(id)
            renderDataWithRegion(region)
        }
    }

    private fun getOnRemoveListener(): MistakesAdapter.OnRemoveListener =
            object: MistakesAdapter.OnRemoveListener{
                override fun onRemove(nameRus: String) {
                    mistakesViewModel.removeMistakeFromDatabase(nameRus)
                }
            }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.search)?.isVisible = true

        val searchItem: MenuItem = menu.findItem(R.id.search)
        val searchView =searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_country) //пишем подсказку в строке поиска
        searchView.setOnQueryTextListener(this)//устанавливаем слушатель
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        //ничего не делаем - не будет фрагмента поиска, так как при вводе символов
        //изменяется список внутри RegionFragment
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        model.updateRegion(Constants.REGION_ALL) // для поиска ставим Все регионы на чипы
        newText?. let {
            if (it.isNotBlank()) {
                val listSearched = mutableListOf<State>()
                for (state in listOfMistakeStates ) {
                    state.nameRus?. let{ nameRus->
                        if((nameRus.toUpperCase(Locale.ROOT)
                                        .startsWith(it.toUpperCase(Locale.ROOT)))){
                            listSearched.add(state)
                        }
                    }
                }
                adapter?.listOfMistakes = listSearched
            }else{
                adapter?.listOfMistakes = listOfMistakeStates
            }
        }
        return false
    }
}