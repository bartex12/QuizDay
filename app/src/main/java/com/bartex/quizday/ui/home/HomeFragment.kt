package com.bartex.quizday.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.ui.adapters.ItemList
import com.bartex.quizday.ui.adapters.HomeAdapter

class HomeFragment : Fragment() {

    private val homeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }
    lateinit var navController: NavController
    lateinit var rv_main: RecyclerView
    private var adapter: HomeAdapter? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        homeViewModel.loadData()
        //наблюдение на всякий случай, вдруг потом данные будут меняться
        homeViewModel.getMainList().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })

        initAdapter(view)

        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private fun initAdapter(view:View) {
        rv_main = view.findViewById(R.id.rv_main)
        rv_main.layoutManager = LinearLayoutManager(requireActivity())
        adapter = HomeAdapter(getOnClickListener())
        rv_main.adapter = adapter
    }

    private fun getOnClickListener(): HomeAdapter.OnitemClickListener =
           object : HomeAdapter.OnitemClickListener{
               override fun onItemClick(position: Int) {
                   when(position){
                       0 -> navController.navigate(R.id.textquizFragment)
                       1 -> navController.navigate(R.id.imagequizFragment)
                   }
               }
           }

    private fun renderData(list: List<ItemList>) {
        adapter?.listOfTypes = list
    }
}