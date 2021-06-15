package com.example.mysecapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        var i = intent.getCharSequenceExtra("text")
        if(intent == null || i.toString() == ""){
            return
        }

        var splitted = ArrayList<String>(i.split(",", ignoreCase = true, limit = 0))
        lvNames.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, splitted)
        lvNames.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "Элемент: ${splitted.get(position)}", Toast.LENGTH_SHORT).show()
        }

    }
}