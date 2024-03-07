package com.example.medicaredoctor



import BookingListAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.example.medicaredoctor.Models.Appointment
import com.example.medicaredoctor.Models.Doctor
import com.example.medicaredoctor.Models.User
import com.example.medicaredoctor.databinding.ActivityMainBinding


import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import de.hdodenhof.circleimageview.CircleImageView

import java.util.Date
import java.util.Locale
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener
    {

    private var bottomNav: BottomNavigationView? = null
    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String
    private lateinit var mdoctordetail: Doctor

    private lateinit var mSharedPreferences: SharedPreferences
    lateinit var activebookinglist : ArrayList<Appointment>
    val specialityList = listOf("Surgeon", "Dentist", "Neurologist", "Cardiologist", "Gynaecologist")

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        FirestoreClass().getusersList(this@MainActivity)
        FirestoreClass().loadUserData(this, true)
        showProgressDialog("Please Wait")

        bottomNav = findViewById(R.id.bottomNav)
        binding?.navView?.setNavigationItemSelectedListener(this)

        val selectedItemId = intent.getIntExtra("selectedItemId", -1)
        if (selectedItemId != -1) {
            bottomNav?.selectedItemId = selectedItemId
        } else {
            bottomNav?.selectedItemId = R.id.bookings
        }

        bottomNav?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.slots -> {
                    startActivityForResult(Intent(this, SlotsActivity::class.java), MY_PROFILE_REQUEST_CODE)
                    true
                }
//                R.id.settings -> {
//                    startActivityForResult(Intent(this, SettingActivity::class.java), MY_PROFILE_REQUEST_CODE)
//                    true
//                }
//                R.id.webview_chatbot -> {
//                    startActivityForResult(Intent(this, SettingActivity::class.java), MY_PROFILE_REQUEST_CODE)
//                    true
//                }
                else -> false
            }
        }

        setupActionBar()
//        categoriesSet()
        findViewById<CircleImageView>(R.id.profileImage).setOnClickListener {
            startActivityForResult(Intent(this, SettingActivity::class.java), MY_PROFILE_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun populatesbookingListToUI(booking_List: ArrayList<Appointment>) {
        hideProgressDialog()

        // Update activebookinglist with the sorted list


        // Sort the booking_List based on date and time
        val sortedBookingList = booking_List.sortedWith(compareBy<Appointment> { parseDate(it.date) }.thenBy { parseTime(it.time) })
        activebookinglist = ArrayList(sortedBookingList)
        Log.e("populateBoardsListToUI", "Doctor List: $sortedBookingList")

        val rv_speciality_list = findViewById<RecyclerView>(R.id.bookingcardrecyclerView)
        rv_speciality_list.layoutManager = LinearLayoutManager(this@MainActivity)

        // Create an instance of BookingListAdapter and pass the sorted list to it.
        val adapter = BookingListAdapter(this@MainActivity, sortedBookingList)
        rv_speciality_list.adapter = adapter
    }

        private fun parseDate(dateString: String): Date {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return format.parse(dateString) ?: Date()
        }

        private fun parseTime(timeString: String): Date {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.parse(timeString) ?: Date()
        }
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
        mdoctordetail = user
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
            R.id.BMI_CALCULATOR -> {
                val intent = Intent(this, History::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

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
        @RequiresApi(Build.VERSION_CODES.O)
        fun addtobookinglist(position:Int){
            showProgressDialog("Please Wait")
            val bookingdonelist: ArrayList<Appointment> = ArrayList(mdoctordetail.bookingappointment)
            activebookinglist[position].status = "Appointment Succesfull"
            bookingdonelist.add(activebookinglist[position])
            var userappointmentid: String = activebookinglist[position].user_id
            Log.e("appointmentid", "${userappointmentid}")

            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(userappointmentid)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    var bookedarraylist: ArrayList<AppointmentUser> = ArrayList()
                    var appointmentdonelist: ArrayList<AppointmentUser>  = ArrayList()

                    if (user != null) {
                        bookedarraylist = user.userappointment
                        appointmentdonelist = user.bookingappointment
                    }

                    if (bookedarraylist != null) {
                        val indexToRemove = bookedarraylist.indexOfFirst { it.id == bookedarraylist[position].id }

                        // Safety check for the index
                        if (indexToRemove != -1) {
                            // Add the removed item to appointmentdonelist
                            bookedarraylist[indexToRemove].status = "Appointment Done"
                            appointmentdonelist.add(bookedarraylist[indexToRemove])

                            // Remove the item from bookedarraylist
                            bookedarraylist.removeAt(indexToRemove)

                            // Update Firestore only if the removal is successful
                            val userHashMap = HashMap<String, Any>()
                            userHashMap[Constants.USERAPPOINTMENT] = bookedarraylist
                            userHashMap["bookingappointment"] = appointmentdonelist

                            FirebaseFirestore.getInstance().collection(Constants.USERS)
                                .document(userappointmentid)
                                .update(userHashMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "USER Will be Notified Shortly", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("FirestoreUpdateError", "Error updating Firestore: ${exception.message}", exception)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreQueryError", "Error fetching user document: ${exception.message}", exception)
                }
            val activelist: ArrayList<Appointment> = activebookinglist
            activelist.removeAt(position)

            val doctorHashMap = HashMap<String, Any>()

            doctorHashMap[Constants.APPOINTMENT] = activelist
            doctorHashMap["bookingappointment"] = bookingdonelist


            FirestoreClass().updatedoneappointmentlist(
                this@MainActivity,
                doctorHashMap,
                FirestoreClass().getCurrentUserID()
            )

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun addtoexpirelist(position:Int){
            showProgressDialog("Please Wait")
            val expiredlist: ArrayList<Appointment> = ArrayList(mdoctordetail.expiredappointment)
            activebookinglist[position].status = "Expired"
            expiredlist.add(activebookinglist[position])
            var userappointmentid: String = activebookinglist[position].user_id
            Log.e("appointmentid", "${userappointmentid}")

            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(userappointmentid)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)
                    var bookedarraylist: ArrayList<AppointmentUser> = ArrayList()
                    var appointmentexpiredlist: ArrayList<AppointmentUser>  = ArrayList()

                    if (user != null) {
                        bookedarraylist = user.userappointment
                        appointmentexpiredlist = user.expiredappointment
                    }

                    if (bookedarraylist != null) {
                        val indexToRemove = bookedarraylist.indexOfFirst { it.id == bookedarraylist[position].id }

                        // Safety check for the index
                        if (indexToRemove != -1) {
                            // Add the removed item to appointmentdonelist
                            bookedarraylist[indexToRemove].status = "Appointment Expired"
                            appointmentexpiredlist.add(bookedarraylist[indexToRemove])

                            // Remove the item from bookedarraylist
                            bookedarraylist.removeAt(indexToRemove)

                            // Update Firestore only if the removal is successful
                            val userHashMap = HashMap<String, Any>()
                            userHashMap[Constants.USERAPPOINTMENT] = bookedarraylist
                            userHashMap["expiredappointment"] = appointmentexpiredlist

                            FirebaseFirestore.getInstance().collection(Constants.USERS)
                                .document(userappointmentid)
                                .update(userHashMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "USER Will be Notified Shortly", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("FirestoreUpdateError", "Error updating Firestore: ${exception.message}", exception)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreQueryError", "Error fetching user document: ${exception.message}", exception)
                }
            val activelist: ArrayList<Appointment> = activebookinglist
            activelist.removeAt(position)

            val doctorHashMap = HashMap<String, Any>()

            doctorHashMap[Constants.APPOINTMENT] = activelist
            doctorHashMap["expiredappointment"] = expiredlist


            FirestoreClass().updatedoneappointmentlist(
                this@MainActivity,
                doctorHashMap,
                FirestoreClass().getCurrentUserID()
            )

        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun successappointmentdone(){
            hideProgressDialog()
            FirestoreClass().loadUserData(this, true)
            FirestoreClass().getusersList(this)
        }



}
