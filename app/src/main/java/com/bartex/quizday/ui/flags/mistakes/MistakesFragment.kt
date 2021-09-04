package com.bartex.quizday.ui.flags.mistakes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.Fragment
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
import com.bartex.quizday.ui.flags.FlagsViewModel
import com.google.android.material.chip.Chip
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

    private val flagsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
    }
    private var listOfMistakeStates  = mutableListOf<State>() //список стран региона с ошибками
    private lateinit var rvStatesMistake: RecyclerView
    private lateinit  var emptyViewMistake: TextView
    private lateinit var chipGroupMistake: ChipGroup
    private var region:String = Constants.REGION_ALL

    companion object {
        const val TAG = "33333"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "MistakesFragment onCreateView ")
        return inflater.inflate(R.layout.fragment_mistakes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "MistakesFragment onViewCreated ")

        navController = Navigation.findNavController(view)

        //восстанавливаем позицию списка после поворота или возвращения на экран
        position =  mistakesViewModel.getPositionState()

        initViews(view)
        initAdapter()
        initChipGroupListener()

        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()

        //чтобы получить текущий регион - сделал обмен данными через flagsViewModel
        // во flagsViewModel в методе resetQuiz() кладём значение, а здесь принимаем
        flagsViewModel.getDataFlagsToRegionFragment()
            .observe(viewLifecycleOwner, {data->
                region = data.region //текущий регион
                chipGroupMistake.check(getRegionId(region))
                //не убирать эту строку иначе при повороте данные пропадают!
                renderDataWithRegion(region)
            })

        //получаем все ошибки автоматически при любом изменении в базе данных
        mistakesViewModel.getAllMistakesLive()
                .observe(viewLifecycleOwner, {
                    listOfMistakeStates =   it.map {room->
                        State(capital =room.capital, flag = room.flag,name =room.name,
                            region = room.region, nameRus = room.nameRus,
                            capitalRus = room.capitalRus, regionRus = room.regionRus
                        )
                    } as MutableList<State>
                    chipGroupMistake.check(getRegionId(region))
                    renderDataWithRegion(region)
                })
    }


    private fun initViews(view: View) {
        rvStatesMistake = view.findViewById(R.id.rv_states_mistakes)
        emptyViewMistake = view.findViewById(R.id.empty_view_mistakes)
        chipGroupMistake = view.findViewById(R.id.chip_region_mistakes)

    }

    //запоминаем  позицию списка, на которой сделан клик - на случай поворота экрана
    override fun onPause() {
        super.onPause()
        //определяем первую видимую позицию
        val manager = rvStatesMistake.layoutManager as LinearLayoutManager
        val firstPosition = manager.findFirstVisibleItemPosition()
        mistakesViewModel.savePositionState(firstPosition)
        Log.d(TAG, "MistakesFragment onPause firstPosition = $firstPosition")
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
        }else{
            rvStatesMistake.visibility =  View.VISIBLE
            emptyViewMistake.visibility = View.GONE

            listOfMistakeStates.sortBy {it.nameRus}
            adapter?.listOfMistakes = listOfMistakeStates

            rvStatesMistake.layoutManager?.scrollToPosition(position) //крутим в запомненную позицию списка
            Log.d(TAG, "MistakesFragment renderData scrollToPosition = $position")
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
            val newRegion: String = getRegionName(id)
            renderDataWithRegion(newRegion)
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

    private fun getRegionId(region: String): Int {
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

    private fun getRegionName(id: Int): String {
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
}