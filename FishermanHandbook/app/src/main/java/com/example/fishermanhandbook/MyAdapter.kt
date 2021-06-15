package com.example.fishermanhandbook

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyAdapter (listArray: ArrayList<ListItem>, context:Context):
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    var listArray = listArray
    var context = context

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtDesc = view.findViewById<TextView>(R.id.txtDescription)
        val img = view.findViewById<ImageView>(R.id.imaItemIcon)

        fun bind(listItem: ListItem, context: Context){
            txtName.setText(listItem.titleText)
            txtDesc.setText(listItem.descriptionText)
            img.setImageResource(listItem.imageId)
            //img.setImageResource(R.drawable.ic_header_menu)

            itemView.setOnClickListener(){
                val i = Intent(context, ContentActivity::class.java).apply {
                    putExtra("title", txtName.text.toString())
                    putExtra("desc", txtDesc.text.toString())
                    putExtra("image", listItem.imageId)
                }
                context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_layout, parent,false))
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listArray[position],context)
    }

    fun updateAdapter(listArrayTemp : List<ListItem>) {
        listArray.clear()
        listArray.addAll(listArrayTemp)
        notifyDataSetChanged()
    }
}