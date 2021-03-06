package com.bartex.quizday.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.ui.imagequiz.ItemListPicture

class HomeAdapter(private val onItemClickListener: OnitemClickListener,):RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    var listOfTypes : List<ItemListPicture> = listOf()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    interface OnitemClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main,parent, false )
    return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfTypes[position])

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
      return  listOfTypes.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        fun bind(data: ItemListPicture){
            itemView.findViewById<TextView>(R.id.tv_name).text = data.title
            itemView.findViewById<ImageView>(R.id.iv_main).setImageDrawable(data.image)
        }
    }
}