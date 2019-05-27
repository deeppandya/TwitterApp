package com.example.twitterapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.twitterapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSectionA: Button = findViewById(R.id.btnSectionA)
        btnSectionA.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val btnSectionB: Button = findViewById(R.id.btnSectionB)
        btnSectionB.setOnClickListener {
            val intent = Intent(this, TweetSearchActivity::class.java)
            startActivity(intent)
        }
    }
}
