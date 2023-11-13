package com.example.medicaredoctor



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.medicaredoctor.Models.Doctor
import com.example.medicaredoctor.databinding.ActivityMainBinding


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener
    {

    private var bottomNav: BottomNavigationView? = null
    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String
    private lateinit var mSharedPreferences: SharedPreferences
    val specialityList = listOf("Surgeon", "Dentist", "Neurologist", "Cardiologist", "Gynaecologist")

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        mSharedPreferences = this.getSharedPreferences(
            Constants.MEDICARE_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)

        if (tokenUpdated) {
            // Get the current logged in user details.
            FirestoreClass().loadUserData(this@MainActivity, true)
        } else {
//            FirebaseMessaging.getInstance().token.addOnSuccessListener { instanceResult ->
//                updateFCMToken(instanceResult)
//            }
        }
//        newsuiset()
        FirestoreClass().loadUserData(this, true)
        showProgressDialog("Please Wait")

        bottomNav = findViewById(R.id.bottomNav)
        binding?.navView?.setNavigationItemSelectedListener(this)

        val selectedItemId = intent.getIntExtra("selectedItemId", -1)
        if (selectedItemId != -1) {
            bottomNav?.selectedItemId = selectedItemId
        } else {
            bottomNav?.selectedItemId = R.id.home
        }

        bottomNav?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bookings -> {
                    startActivityForResult(Intent(this, SlotsActivity::class.java), MY_PROFILE_REQUEST_CODE)
                    true
                }
//                R.id.settings -> {
//                    startActivityForResult(Intent(this, SettingActivity::class.java), MY_PROFILE_REQUEST_CODE)
//                    true
//                }
                R.id.webview_chatbot -> {
                    startActivityForResult(Intent(this, SettingActivity::class.java), MY_PROFILE_REQUEST_CODE)
                    true
                }
                else -> false
            }
        }

        setupActionBar()
//        categoriesSet()
        findViewById<CircleImageView>(R.id.profileImage).setOnClickListener {
            startActivityForResult(Intent(this, SettingActivity::class.java), MY_PROFILE_REQUEST_CODE)
        }
    }

//    private fun categoriesSet() {
//        val rvBoardsList = findViewById<RecyclerView>(R.id.smallCardRecyclerView)
//
//        val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
//        rvBoardsList.layoutManager = layoutManager
//        rvBoardsList.setHasFixedSize(true)
//
//        val adapter = DemoAdapter(specialityList, this@MainActivity)
//        rvBoardsList.adapter = adapter
//
//        adapter.setOnClickListener(object :
//            DemoAdapter.OnClickListener {
//            override fun onClick(position: Int) {
//                val intent = Intent(this@MainActivity, Details_of_Doctors::class.java)
//                intent.putExtra(Constants.SPECIALITY, specialityList[position])
//                startActivity(intent)
//            }
//        })
//    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this, true)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    fun updateNavigationUserDetails(user: Doctor, readBoardList: Boolean) {
        hideProgressDialog()
        mUserName = user.name

        val navUserImage = findViewById<CircleImageView>(R.id.profileImage)
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)


        val tvUserName = findViewById<TextView>(R.id.signedinuseret)
        val userName = user.name
        val truncatedUserName = if (userName.length > 10) {
            userName.substring(0, 10) + "..."
        } else {
            userName
        }
        tvUserName.text = "Hi!! $truncatedUserName"
    }

    private fun toggleDrawer() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {

        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.BMI_CALCULATOR -> {
//                val intent = Intent(this, BMIActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this, DoctorsLoginPage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
    fun tokenUpdateSuccess() {

        hideProgressDialog()

        // Here we have added a another value in shared preference that the token is updated in the database successfully.
        // So we don't need to update it every time.
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()

        // Get the current logged in user details.
        // Show the progress dialog.
//        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this@MainActivity, true)
    }
    private fun updateFCMToken(token: String){
        val userHashMap = HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN] = token
//        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }



}
