package com.example.medicaredoctor

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SlotsActivity : AppCompatActivity() {

    private lateinit var timeSlots: MutableList<String>
    private lateinit var adapter: TimeSlotAdapter
    private lateinit var btnSave: Button
    private var changesMade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slots)
        setupActionBar()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        timeSlots = mutableListOf()
        adapter = TimeSlotAdapter(
            timeSlots,
            this::deleteTimeSlot,
            this::showEditTimeDialog
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        FirestoreClass().getslotList(this@SlotsActivity)
        btnSave = findViewById(R.id.btn_save_time_slot)
        enableSaveButton(false)  // Initially, disable the save button

        // Other setup...
    }

    fun loadslotlist(timeslotlist: MutableList<String>) {
        timeSlots.clear()
        timeSlots.addAll(timeslotlist)
        adapter.notifyDataSetChanged()
    }

    fun onAddTimeSlotButtonClick(view: View) {
        showTimePickerDialog()
    }


    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                val formattedTime = formatTime(hourOfDay, minute)
                addTimeSlot(formattedTime)
            },
            hourOfDay, minute, false
        )

        timePickerDialog.show()
    }

    private fun showDiscardChangesDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Discard Changes")
        alertDialogBuilder.setMessage("Do you want to discard the changes?")
        alertDialogBuilder.setPositiveButton("Discard") { _, _ ->
            super.onBackPressed()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    override fun onBackPressed() {
        if (changesMade) {
            showDiscardChangesDialog()
        } else {
            // No changes made, proceed with navigation
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("selectedItemId", R.id.bookings)
            startActivity(intent)
            finish()
        }
    }


    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_slots_activity)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        actionbar?.title = "Time Slots"
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun onSaveTimeSlotButtonClick(view: View){
        FirestoreClass().updateTimeslot(this@SlotsActivity, timeSlots)

    }
    fun disablebutton(){
        btnSave = findViewById(R.id.btn_save_time_slot)
        btnSave.isEnabled = false
        changesMade = false
    }
    private fun formatTime(hourOfDay: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun addTimeSlot(timeSlot: String) {
        timeSlots.add(timeSlot)
        adapter.notifyDataSetChanged()
        changesMade = true
        enableSaveButton(true)
    }

    private fun deleteTimeSlot(position: Int) {
        timeSlots.removeAt(position)
        adapter.notifyDataSetChanged()
        changesMade = true
        enableSaveButton(true)
    }

    private fun updateTimeSlot(position: Int, time: String) {
        timeSlots[position] = time
        adapter.notifyItemChanged(position)
        changesMade = true
        enableSaveButton(true)
    }

    private fun enableSaveButton(enable: Boolean) {
        btnSave.isEnabled = enable
    }

    private fun showEditTimeDialog(position: Int) {
        val currentTime = timeSlots[position].split(":")
        val hour = currentTime[0].toInt()
        val minute = currentTime[1].split(" ")[0].toInt()
        val isAM = currentTime[1].split(" ")[1] == "AM"

        val context = this  // Use 'this' instead of 'holder.itemView.context'

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val formattedTime = formatTime(hourOfDay, minute)
                updateTimeSlot(position, formattedTime)
            },
            hour, minute, false
        )

        timePickerDialog.show()
    }
}

class TimeSlotAdapter(
    private val timeSlots: MutableList<String>,
    private val deleteTimeSlot: (Int) -> Unit,
    private val showEditTimeDialog: (Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTimeSlot: TextView = itemView.findViewById(R.id.textViewTimeSlot)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val timeSlot = timeSlots[position]
        holder.textViewTimeSlot.text = timeSlot

        holder.buttonEdit.setOnClickListener {
            showEditTimeDialog(position)
        }

        holder.buttonDelete.setOnClickListener {
            deleteTimeSlot(position)
        }
    }

    override fun getItemCount(): Int {
        return timeSlots.size
    }
}
