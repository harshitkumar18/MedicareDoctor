package com.example.medicaredoctor

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicaredoctor.Models.Appointment

class History : BaseActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
              setupActionBar()
            // Get the current logged in user details.
        val spinnerFilter = findViewById<Spinner>(R.id.spinner_filter_appointment)

        // Set up a listener for Spinner item selection
        spinnerFilter.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Get the selected filter text
                val selectedFilter = parentView.getItemAtPosition(position).toString()

                // Call FirestoreClass().getappointmenthistoryList with the new selected filter
                FirestoreClass().getappointmenthistoryList(this@History, selectedFilter)

                // Show progress dialog or perform any other actions if needed
                showProgressDialog("Please Wait")
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing if nothing is selected
            }
        })
    }


    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_history)
        setSupportActionBar(toolbar)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)


            val title = SpannableString("History")
            title.setSpan(ForegroundColorSpan(Color.WHITE), 0, title.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            actionbar.title = title
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }





    fun populatesbookingHistoryListToUI(booking_List: ArrayList<Appointment>) {
        hideProgressDialog()
        Log.e("populateBoardsListToUI", "Doctor List: $booking_List")

        val rv_speciality_list = findViewById<RecyclerView>(R.id.historyrecyclerView)
        rv_speciality_list.layoutManager = LinearLayoutManager(this@History)

//        rv_speciality_list.setHasFixedSize(true)

        // Create an instance of DoctorListAdapter and pass the doctor_List to it.
        val adapter = BookingHistoryListAdapter(this@History, booking_List)
        rv_speciality_list.adapter = adapter // Attach the adapter to the recyclerView.

//
    }
    }
