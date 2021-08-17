package com.bartex.quizday.ui.imagequiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.MainActivity
import com.bartex.quizday.R
import com.bartex.quizday.model.MainList
import com.bartex.quizday.ui.home.HomeAdapter

class ImageQuizFragment : Fragment() {

    private lateinit var imageViewModel: ImageQuizViewModel

    lateinit var navController: NavController
    lateinit var rv_image: RecyclerView
    private var adapter: HomeAdapter? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_imagquiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        imageViewModel =
                ViewModelProvider(this).get(ImageQuizViewModel::class.java)

        imageViewModel.loadData()
        //наблюдение на всякий случай, вдруг потом данные будут меняться
        imageViewModel.getImageList().observe(viewLifecycleOwner, Observer {
            renderData(it)
        })

        initAdapter(view)

        //приводим меню тулбара в соответствии с onPrepareOptionsMenu в MainActivity
        //без этой строки меню в тулбаре ведёт себя неправильно
        setHasOptionsMenu(true)
        requireActivity().invalidateOptionsMenu()
    }

    private fun initAdapter(view:View) {
        rv_image = view.findViewById(R.id.rv_image)
        rv_image.layoutManager = LinearLayoutManager(requireActivity())
        adapter = HomeAdapter(getOnClickListener())
        rv_image.adapter = adapter
    }

    private fun renderData(list: List<MainList>) {
        adapter?.listOfTypes = list
    }

    //todo
    private fun getOnClickListener(): HomeAdapter.OnitemClickListener =
            object :HomeAdapter.OnitemClickListener{
                override fun onItemClick(position: Int) {
                    when(position){
                        0 -> navController.navigate(R.id.action_imagequizFragment_to_flagsFragment)
                        1 -> Toast.makeText(requireActivity(),
                                requireActivity().resources.getString(R.string.no_now), Toast.LENGTH_SHORT).show()
                        2 -> Toast.makeText(requireActivity(),
                                requireActivity().resources.getString(R.string.no_now), Toast.LENGTH_SHORT).show()
                    }
                }
            }
}