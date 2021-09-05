package com.bartex.quizday.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bartex.quizday.R
import com.bartex.quizday.ui.adapters.ItemList
import com.squareup.picasso.Picasso

class HomeListAdapter(private val context: Context,
                      private val dataSource: MutableList<ItemList>):BaseAdapter()  {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
       return dataSource.size
    }

    override fun getItem(position: Int): Any {
      return  dataSource[position]
    }

    override fun getItemId(position: Int): Long {
      return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.item_state, parent, false)

        val titleTextView = rowView.findViewById(R.id.tv_name_list) as TextView
        val imageView = rowView.findViewById(R.id.iv_flag_list) as ImageView

        val itemList = getItem(position) as ItemList
        titleTextView.text = itemList.title
        itemList.image?. let{
            Picasso.with(context).load(it).placeholder(R.mipmap.ic_launcher).into(imageView)
        }
        return rowView
    }
}