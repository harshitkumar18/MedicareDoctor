import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicaredoctor.Constants
import com.example.medicaredoctor.Models.Appointment
import com.example.medicaredoctor.R
import com.example.medicaredoctor.User

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class BookingListAdapter(
    private val context: Context,
    private var list: ArrayList<Appointment>
) : RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {

//    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivuserrImage: ImageView = view.findViewById(R.id.item_bookeddoctor_image)
        val tvuserrName: TextView = view.findViewById(R.id.item_tv_name_doctor_details)
        val tvBookingTimeDate: TextView = view.findViewById(R.id.booking_time_and_date)
        val tvbookingid : TextView = view.findViewById(R.id.booking_id)
        val bookingStatus : TextView = view.findViewById(R.id.booking_status)
        val cancel_appointment : Button = view.findViewById(R.id.cancel_appointment)
        val reschedule: Button = view.findViewById(R.id.buttonReschedule)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(model.user_id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val doctorDetails = documentSnapshot.toObject(User::class.java)

                if (doctorDetails != null) {
                    // Populate the views with doctor details
                    Glide.with(context)
                        .load(doctorDetails.image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(holder.ivuserrImage)

                    holder.tvuserrName.text = doctorDetails.name
                    holder.tvbookingid.text = "Booking id: ${model.id}"
                    holder.tvBookingTimeDate.text = "Time: ${model.time}, Date: ${model.date}"
                    holder.bookingStatus.text = "Status: ${model.status}"

                }
            }
    }

        override fun getItemCount(): Int {
            return list.size
        }
    }

//    fun setOnClickListener(onClickListener: OnClickListener) {
//        this.onClickListener = onClickListener
//    }

//    interface OnClickListener {
//        fun onClick(position: Int, model: Appointment)
//    }

//    private fun setAnimation(view: View) {
//        val anim = AlphaAnimation(0.0f, 1.0f)
//        anim.duration = 1000
//        view.startAnimation(anim)
//    }

