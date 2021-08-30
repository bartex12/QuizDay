package com.bartex.quizday.ui.flags.regions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.model.entity.State
import com.bartex.quizday.ui.adapters.RegionAdapter
import com.bartex.quizday.ui.adapters.SvgImageLoader
import com.bartex.quizday.ui.flags.FlagsViewModel
import java.util.*


class RegionFragment : Fragment(),
        SearchView.OnQueryTextListener {

    private var position = 0
    private var adapter: RegionAdapter? = null
    private lateinit var navController: NavController
    private val regionViewModel by lazy{
        ViewModelProvider(requireActivity()).get(RegionViewModel::class.java)
    }
    private var listOfStates = mutableListOf<State>()
    private lateinit var rvStatesRegion: RecyclerView
    private lateinit var emptyViewRegion: TextView

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

        //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
        //без этой строки меню в тулбаре ведёт себя неправильно
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()

        val flagsViewModel = ViewModelProvider(requireActivity()).get(FlagsViewModel::class.java)
       flagsViewModel.getCurrentLostOfFlags()
               .observe(viewLifecycleOwner, {
                   listOfStates = it.listStates
                   Toast.makeText(requireActivity(), "${listOfStates.size}", Toast.LENGTH_SHORT).show()
                   renderData(listOfStates)
               })
    }

    private fun initViews(view: View) {
        rvStatesRegion =  view.findViewById(R.id.rv_states_region)
        emptyViewRegion =  view.findViewById(R.id.empty_view_region)
    }

    //запоминаем  позицию списка, на которой сделан клик - на случай поворота экрана
    override fun onPause() {
        super.onPause()
        //определяем первую видимую позицию
        val manager = rvStatesRegion.layoutManager as LinearLayoutManager
        val firstPosition = manager.findFirstVisibleItemPosition()
        regionViewModel.savePositionState(firstPosition)
        Log.d(TAG, "StatesFragment onPause firstPosition = $firstPosition")
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
            listOfStates
            listOfStates.sortBy { it.nameRus }
            listOfStates
            adapter?.listOfRegion = listOfStates
            rvStatesRegion.layoutManager?.scrollToPosition(position) //крутим в запомненную позицию списка
            Log.d(TAG, "StatesFragment renderState scrollToPosition = $position")
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
        //значок лупы слева в развёрнутом сост и сворачиваем строку поиска (true)
        //searchView.setIconifiedByDefault(true)
        //пишем подсказку в строке поиска
        searchView.queryHint = getString(R.string.search_country)
        //устанавливаем в панели действий кнопку ( > )для отправки поискового запроса
        // searchView.isSubmitButtonEnabled = true
        //устанавливаем слушатель
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        //ничего не делаем - не будет фрагмента поиска, так как при вводе символов
        //изменяется список внутри StatesFragment
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?. let {
            if (it.isNotBlank()) {
                val listSearched = mutableListOf<State>()
                    for (state in listOfStates) {
                        state.nameRus?. let{ nameRus->
                            if((nameRus.toUpperCase(Locale.ROOT)
                                            .startsWith(it.toUpperCase(Locale.ROOT)))){
                                listSearched.add(state)
                            }
                        }
                    }
                adapter?.listOfRegion = listSearched
            }else{
                adapter?.listOfRegion = listOfStates
            }
        }
        return false
    }
}