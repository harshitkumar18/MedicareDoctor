package com.example.medicaredoctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class DoctorSplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this, DoctorsSignUpPage::class.java))
                finish()
            }, 3000
        )
    }
}