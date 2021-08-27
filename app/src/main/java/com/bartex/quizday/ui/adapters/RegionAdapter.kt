package com.bartex.quizday.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R

class RegionAdapter(private val onItemClickListener: OnItemClickListener,)
    :RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    var listOfRegion : List<ItemList> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_region,parent, false )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfRegion[position])

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return  listOfRegion.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(data: ItemList){
            itemView.findViewById<TextView>(R.id.tv_region_name).text = data.title
            itemView.findViewById<ImageView>(R.id.iv_region).setImageDrawable(data.image)
        }
    }
}