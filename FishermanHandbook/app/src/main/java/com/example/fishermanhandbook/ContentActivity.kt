package com.example.fishermanhandbook

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_layout.*

class ContentActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_layout)

        contTvTitle.text = intent.getStringExtra("title")
        contTvDesc.text = intent.getStringExtra("desc")
        contMainImg.setImageResource(intent.getIntExtra("image", R.drawable.ic_header_menu))
    }
}