package com.example.medicaredoctor

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.medicaredoctor.databinding.ActivityDoctorsLoginPageBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class DoctorsLoginPage : AppCompatActivity() {
    private lateinit var binding:ActivityDoctorsLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var Firebasestore: FirebaseFirestore
    private lateinit var databaseReference:FirebaseDatabase
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gotoSignupPage.setOnClickListener{
            startActivity(Intent(this,DoctorsSignUpPage::class.java))
        }
        binding.signinuser.setOnClickListener {
            val Firebasestore = FirebaseFirestore.getInstance()

            val doctorPassword = binding.doctorsPassword.text.toString()
            val db = Firebasestore.collection("DoctorsData")
            val doctorEmail = binding.DoctorsID.text.toString()
                    if (doctorEmail != null) {
                        login(doctorEmail,doctorPassword)
                        Toast.makeText(this,"Successfully login",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,"No email found",Toast.LENGTH_SHORT).show()
                    }
                }
            }



    private fun login(email: String, doctorPassword: String) {
         firebaseAuth = FirebaseAuth.getInstance()
         firebaseAuth.signInWithEmailAndPassword(email,doctorPassword).addOnCompleteListener(this){task->
             if(task.isSuccessful){
                 Toast.makeText(this,"Successfully login",Toast.LENGTH_SHORT).show()
                 val intent  = Intent(this,MainActivity::class.java)
                 startActivity(intent)
             }
             else
             {
                 val errorMessage = task.exception
                 Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_LONG).show()

             }
         }
    }
}

