package com.example.fishermanhandbook

import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var adapter : MyAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav_view.setNavigationItemSelectedListener(this)
        var temp = nav_view.getHeaderView(0).findViewById<ImageView>(R.id.imageView1)
        temp.setImageResource(R.drawable.ic_header_menu)

        var list = ArrayList<ListItem>()

        list.addAll(fillArrays(resources.getStringArray(R.array.fish),resources.getStringArray(R.array.fish_desc),
            getImageId(R.array.fish_image)))
        adapter = MyAdapter(list,this)

        rcView.hasFixedSize()
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_fish -> {
                adapter?.updateAdapter(fillArrays(resources.getStringArray(R.array.fish),resources.getStringArray(R.array.fish_desc),
                    getImageId(R.array.fish_image)))
                Toast.makeText(this, "Вы нажали на \"${this.resources.getString(R.string.fish)}\"", Toast.LENGTH_SHORT).show()
            }
            R.id.item_na -> {
                adapter?.updateAdapter(fillArrays(resources.getStringArray(R.array.na),resources.getStringArray(R.array.na_desc),
                    getImageId(R.array.na_image)))
                Toast.makeText(this, "Вы нажали на \"${this.resources.getString(R.string.na)}\"", Toast.LENGTH_SHORT).show()
            }
            R.id.item_sn -> Toast.makeText(this, "Вы нажали на \"${this.resources.getString(R.string.sn)}\"", Toast.LENGTH_SHORT).show()
            R.id.item_stories -> Toast.makeText(this, "Вы нажали на \"${this.resources.getString(R.string.stories)}\"", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    fun fillArrays(titleArray: Array<String>, descArray: Array<String>, imageArray: IntArray) : List<ListItem>{
        var listItemArray = ArrayList<ListItem>()
        for(n in 0..titleArray.size - 1){
            listItemArray.add(ListItem(imageArray[n],titleArray[n],descArray[n]))
        }
        return listItemArray
    }
    fun getImageId(imageArray: Int) : IntArray
    {
        var tArray: TypedArray = resources.obtainTypedArray(imageArray)
        var count = tArray.length()
        val ids = IntArray(count)

        for (i in ids.indices){
            ids[i] = tArray.getResourceId(i, 0)
        }

        tArray.recycle()
        return ids
    }
}