package com.example.trelloc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class Intro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        btn_sign_up_intro.setOnClickListener {
            startActivity(Intent(this,SignUp::class.java))
        }
        btn_sign_in_intro.setOnClickListener {
            startActivity(Intent(this,SignIn::class.java))
        }

    }
}