package com.example.hackathon.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hackathon.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // Make splash screen
        button_text.setOnClickListener {

            // Start register if not register in first time
            var prefs : SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            var fisrtStart = prefs.getBoolean("firstStart", true)
            if(fisrtStart){
                var intent = Intent(this, RegisterActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                var intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            finish()
        }
    }
}
