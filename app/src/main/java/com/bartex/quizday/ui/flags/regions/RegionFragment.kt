package com.bartex.quizday.ui.flags.regions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.model.common.Constants
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.ui.adapters.RegionAdapter
import com.bartex.quizday.ui.adapters.SvgImageLoader
import com.bartex.quizday.ui.flags.FlagsViewModel
import com.google.android.material.chip.ChipGroup
import java.util.*


class RegionFragment : Fragment(),
        SearchView.OnQueryTextListener {

    private var position = 0
    private var adapter: RegionAdapter? = null
    private lateinit var navController: NavController
    private val regionViewModel by lazy{
        ViewModelProvider(requireActivity()).get(RegionViewModel::class.java)
    }

    private val flagsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
    }
        private var listOfRegionStates  = mutableListOf<State>() //список стран региона
        private lateinit var rvStatesRegion: RecyclerView
        private lateinit  var emptyViewRegion: TextView
        private lateinit var chipGroupRegion: ChipGroup
        private lateinit var progressBarRegion: ProgressBar
        private var region:String = ""

        companion object {
        const val TAG = "33333"
    }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            return inflater.inflate(R.layout.fragment_regions, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            navController = Navigation.findNavController(view)

            //восстанавливаем позицию списка после поворота или возвращения на экран
            position =  regionViewModel.getPositionState()

            initViews(view)
            initAdapter()
            initChipGroupListener()

            setHasOptionsMenu(true)
            requireActivity().invalidateOptionsMenu()

            flagsViewModel.getDataFlagsToRegionFragment()
                    .observe(viewLifecycleOwner, {data->
                        listOfRegionStates = data.listStatesFromNet  // полный список стран
                        region = data.region //текущий регион
                        chipGroupRegion.check(getRegionId(region))
                        //не убирать эту строку иначе при повороте данные пропадают!
                        renderDataWithRegion(region)
                    })
        }


    private fun initViews(view: View) {
            progressBarRegion =view.findViewById<ProgressBar>(R.id.progress_bar_region)
            rvStatesRegion =  view.findViewById(R.id.rv_states_region)
            emptyViewRegion =  view.findViewById(R.id.empty_view_region)
            chipGroupRegion =  view.findViewById(R.id.chip_region_region)
        }

        private fun initChipGroupListener() {
            chipGroupRegion.setOnCheckedChangeListener { _, id ->
                chipGroupRegion.check(id)
                val newRegion: String = getRegionName(id)
                renderDataWithRegion(newRegion)
            }
        }

    private fun renderDataWithRegion(newRegion: String) {
        when (newRegion) {
            Constants.REGION_ALL -> {
                renderData(listOfRegionStates)
            }
            else -> {
                val filteredList = listOfRegionStates.filter { state ->
                    state.regionRus == newRegion
                } as MutableList<State>
                renderData(filteredList)
            }
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

    private fun getRegionId(region: String): Int {
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

        //запоминаем  позицию списка, на которой сделан клик - на случай поворота экрана
        override fun onPause() {
            super.onPause()
            //определяем первую видимую позицию
            val manager = rvStatesRegion.layoutManager as LinearLayoutManager
            val firstPosition = manager.findFirstVisibleItemPosition()
            regionViewModel.savePositionState(firstPosition)
            Log.d(TAG, "RegionFragment onPause firstPosition = $firstPosition")
        }

        private fun initAdapter() {
            rvStatesRegion.layoutManager = LinearLayoutManager(requireActivity())
            adapter = RegionAdapter(
                    getOnClickListener(),
                    SvgImageLoader(requireActivity())
            )
            rvStatesRegion.adapter = adapter
        }

        private fun renderData(listOfStates:MutableList<State>) {
            if(listOfStates.isEmpty()){
                rvStatesRegion.visibility = View.GONE
                emptyViewRegion.visibility = View.VISIBLE
            }else{
                rvStatesRegion.visibility =  View.VISIBLE
                emptyViewRegion.visibility = View.GONE

                listOfStates.sortBy { it.nameRus }

                adapter?.listOfRegion = listOfStates
                rvStatesRegion.layoutManager?.scrollToPosition(position) //крутим в запомненную позицию списка
                Log.d(TAG, "RegionFragment renderData scrollToPosition = $position")
            }
        }

        private fun getOnClickListener(): RegionAdapter.OnItemClickListener =
                object : RegionAdapter.OnItemClickListener{
                    override fun onItemClick(state: State) {
                        //чтобы сразу исчезала клавиатура а не после перехода в детали
                        val inputManager: InputMethodManager =
                                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.hideSoftInputFromWindow(
                                requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
                    for (state in listOfRegionStates ) {
                        state.nameRus?. let{ nameRus->
                            if((nameRus.toUpperCase(Locale.ROOT)
                                            .startsWith(it.toUpperCase(Locale.ROOT)))){
                                listSearched.add(state)
                            }
                        }
                    }
                    adapter?.listOfRegion = listSearched
                }else{
                    adapter?.listOfRegion = listOfRegionStates
                }
            }
            return false
        }
}
