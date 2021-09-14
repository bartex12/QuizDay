package com.bartex.quizday.ui.home

  import android.os.Bundle
  import android.view.LayoutInflater
  import android.view.View
  import android.view.ViewGroup
  import android.widget.ListView
  import androidx.fragment.app.Fragment
  import androidx.lifecycle.Observer
  import androidx.lifecycle.ViewModelProvider
  import androidx.navigation.NavController
  import androidx.navigation.Navigation
  import com.bartex.quizday.R
  import com.bartex.quizday.ui.adapters.ItemList

class HomeFragment: Fragment() {

    private val homeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    lateinit var navController: NavController
    lateinit var listView:ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        homeViewModel.loadData()
        //наблюдение на всякий случай, вдруг потом данные будут меняться
        homeViewModel.getMainList().observe(viewLifecycleOwner, {
            renderData(it)
        })

        listView = view.findViewById(R.id.listViewHome)

        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()

        listView.setOnItemClickListener { _, _, position, _ ->
            when(position){
                0 -> navController.navigate(R.id.textquizFragment)
               // 1 -> navController.navigate(R.id.imagequizFragment) //пока исключаем
                1 -> navController.navigate(R.id.tabsFragment)
                2 -> navController.navigate(R.id.settingsFragment)
                3 -> navController.navigate(R.id.helpFragment)
            }
        }
    }

    private fun renderData(list: MutableList<ItemList>) {
        val adapter = HomeListAdapter(requireActivity(), list)
        listView.adapter = adapter
    }

}