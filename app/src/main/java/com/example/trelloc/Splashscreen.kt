package com.example.trelloc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.trelloc.firebase.Firestore

class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        @Suppress("DEPRECATION")

        window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_FULLSCREEN
        /*window.setFlags(
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            @Suppress("DEPRECATION")
             WindowManager.LayoutParams.FLAG_FULLSCREEN

        )*/@Suppress("DEPRECATION")
        Handler().postDelayed({
            val Currentuserid= Firestore().getcurrentuserid()
            if (Currentuserid.isNotEmpty()) {
                // Start the Main Activity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Start the Intro Activity
                startActivity(Intent(this, Intro::class.java))
            }



            finish()
        },1500)

    }

}