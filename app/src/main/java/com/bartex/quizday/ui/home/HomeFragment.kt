package com.bartex.quizday.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.model.MainList

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

        rv_main = view.findViewById(R.id.rv_main)

        navController = Navigation.findNavController(view)
        homeViewModel.loadData()
        //наблюдение на всякий случай, вдруг потом данные будут меняться
        homeViewModel.getMainList().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })

        initAdapter()

        //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
        //без этой строки меню в тулбаре ведёт себя неправильно
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private fun initAdapter() {
        rv_main.layoutManager = LinearLayoutManager(requireActivity())
        adapter = HomeAdapter(getOnClickListener())
        rv_main.adapter = adapter
    }

    private fun getOnClickListener(): HomeAdapter.OnitemClickListener =
           object :HomeAdapter.OnitemClickListener{
               override fun onItemClick(position: Int) {
                   when(position){
                       0 -> navController.navigate(R.id.textquizFragment)
                       1 -> navController.navigate(R.id.imagequizFragment)
                   }
               }
           }

    private fun renderData(list: List<MainList>) {
        adapter?.listOfTypes = list
    }
}