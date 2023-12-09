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
        Handler(Looper.getMainLooper()).postDelayed({

            finish()
        }, 9000)
        Handler().postDelayed(
            {
                var currentUserID = FirestoreClass().getCurrentUserID()
                if(currentUserID.isNotEmpty()){
                    startActivity(Intent(this, MainActivity::class.java))

                }
                else{
                    startActivity(Intent(this, DoctorsLoginPage::class.java))
                }
                finish()
            }, 3000
        )
    }
}