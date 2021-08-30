package com.bartex.quizday.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.model.entity.State

class RegionAdapter(
        private val onItemClickListener: OnItemClickListener,
        val imageLoader: IImageLoader<ImageView>)
    :RecyclerView.Adapter<RegionAdapter.ViewHolder>() {

    var listOfRegion : List<State> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnItemClickListener{
        fun onItemClick(state: State)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_state,parent, false )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfRegion[position])

    }

    override fun getItemCount(): Int {
        return  listOfRegion.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(state: State){
            itemView.findViewById<TextView>(R.id.tv_name_list).text = state.nameRus
            state.flag?. let{
                imageLoader.loadInto(it, itemView.findViewById(R.id.iv_flag_list))

                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(state)
                }
            }
        }
    }
}