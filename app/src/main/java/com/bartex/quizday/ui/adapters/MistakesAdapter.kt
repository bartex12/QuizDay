package com.bartex.quizday.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bartex.quizday.R
import com.bartex.quizday.model.entity.State

class MistakesAdapter(
        private val onRemoveListener: OnRemoveListener,
        val imageLoader: IImageLoader<ImageView>)
    : RecyclerView.Adapter<MistakesAdapter.ViewHolder>() {

    lateinit var context: Context
    var isOPenList = mutableListOf<String>()

    var listOfMistakes: MutableList<State> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface OnRemoveListener{
        fun onRemove(nameRus: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_mistakes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfMistakes[position])
    }

    override fun getItemCount(): Int {
        return listOfMistakes.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNameMistake =  itemView.findViewById<TextView>(R.id.tv_name_mistake)
        private val ivFlagMistake =  itemView.findViewById<AppCompatImageView>(R.id.iv_flag_mistake)
        private val showMistakeName = itemView.findViewById<AppCompatImageView>(R.id.show_mistake_name)
        private val showMistakeGroup =itemView.findViewById<Group>(R.id.show_mistake_group)
        private val showMistakeNameLayout = itemView.findViewById<LinearLayout>(R.id.show_mistake_name_layout)
        private val removeMistake = itemView.findViewById<AppCompatImageView>(R.id.remove_mistake)

        fun bind(state: State) {
            tvNameMistake.text = state.nameRus
            state.flag?.let {imageLoader.loadInto(it, ivFlagMistake)}

            if (!isOPenList.contains(state.nameRus)){
                setInvisibleName()
            }else{
                setVisibleName()
            }

            showMistakeNameLayout.setOnClickListener {
                if (!isOPenList.contains(state.nameRus)){
                    state.nameRus?.let { nameRus -> isOPenList.add(nameRus) }
                    setVisibleName()

                }else{
                    isOPenList.remove(state.nameRus)
                    setInvisibleName()
                }
            }

            removeMistake.setOnClickListener {
                state.nameRus?.let {nameRus ->
                            removeItem(nameRus)
                            onRemoveListener.onRemove(nameRus)
                }
                state.nameRus?.let {  }
            }
        }

        private fun setInvisibleName() {
            showMistakeGroup.visibility = View.GONE
            showMistakeName.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_visibility_24
                )
            )
        }

        private fun setVisibleName() {
            showMistakeGroup.visibility = View.VISIBLE
            showMistakeName.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_visibility_off_24
                )
            )
        }

        private fun removeItem(nameRus:String) {
            isOPenList.remove(nameRus)
            listOfMistakes.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }
    }
}