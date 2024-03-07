import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicaredoctor.Constants
import com.example.medicaredoctor.MainActivity
import com.example.medicaredoctor.Models.Appointment
import com.example.medicaredoctor.R
import com.example.medicaredoctor.Models.User

import com.google.firebase.firestore.FirebaseFirestore

class BookingListAdapter(
    private val context: Context,
    private var list: List<Appointment>
) : RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {

//    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivuserrImage: ImageView = view.findViewById(R.id.item_bookeddoctor_image)
        val tvuserrName: TextView = view.findViewById(R.id.item_tv_name_doctor_details)
        val tvBookingTimeDate: TextView = view.findViewById(R.id.booking_time_and_date)
        val tvbookingid : TextView = view.findViewById(R.id.booking_id)
        val bookingStatus : TextView = view.findViewById(R.id.booking_status)
        val expire_appointment : Button = view.findViewById(R.id.expire_appointment)
        val done: Button = view.findViewById(R.id.donebutton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item_card, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    holder.done.setOnClickListener {

                        if(context is MainActivity){
                            context.addtobookinglist(position)
                        }



                    }
                    holder.expire_appointment.setOnClickListener {
                        if(context is MainActivity) {
                            context.addtoexpirelist(position)
                        }
                    }


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

