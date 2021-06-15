package com.example.myapplication

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : Activity() {
    private var img: ImageView? = null
    var counter: Int = 0
    var timer: Timer? = null
    var running: Boolean = false
    var mass: IntArray = intArrayOf(R.drawable.sem_red,R.drawable.sem_yellow,R.drawable.sem_green)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        img = findViewById(R.id.imSem)
    }

    fun onClickWork(view: View){
        view as ImageButton
        if(!running){
            view.setImageResource(R.drawable.btn_stop)
            StartStop()
            running = true
        }
        else{
            view.setImageResource(R.drawable.btn_start)
            img?.setImageResource(R.drawable.sem_gray)
            timer?.cancel()
            running = false
            counter = 0
        }
    }
    fun StartStop(){
        timer = Timer()
        timer?.schedule(object: TimerTask(){
            override fun run() {
                runOnUiThread{img?.setImageResource(mass[counter++%3])}
            }

        }, 0, 1000)
    }
}

