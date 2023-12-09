package com.example.medicaredoctor


import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.medicaredoctor.Models.Appointment
import com.example.medicaredoctor.Models.Doctor


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FirestoreClass {

    private val mfirestore = FirebaseFirestore.getInstance()

//    fun registerUser(activity: Details, userInfo: User){
//         mfirestore.collection(Constants.USERS)
//             .document(getCurrentUserID())
//             .set(userInfo, SetOptions.merge())
//             .addOnSuccessListener {
//                   activity.userRegisteredSuccess()
//             }
//    }
@RequiresApi(Build.VERSION_CODES.O)
fun updatedoneappointmentlist(activity: MainActivity, doctorHashMap: HashMap<String, Any>, documentId: String) {
    mfirestore.collection(Constants.DOCTOR) // Collection Name
        .document(documentId) // Document ID
        .update(doctorHashMap) // A hashmap of fields which are to be updated.
        .addOnSuccessListener {
            // Profile data is updated successfully.
            Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

            Toast.makeText(activity, "Item Removed From Active List", Toast.LENGTH_SHORT).show()
            activity.successappointmentdone()

        }
}
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mfirestore.collection(Constants.DOCTOR)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(Doctor::class.java)
                when(activity){
//                    is SignInActivity ->{
//                        if(loggedInUser!=null){
//                            activity.signInSuccess(loggedInUser)
//                        }
//                    }
                    is MainActivity ->{
                        if (loggedInUser != null) {
                            activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
                        }
                    }
                    is SettingActivity ->{
                        if (loggedInUser != null) {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }

                }


            }
    }
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mfirestore.collection(Constants.DOCTOR) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Notify the success result.
                when(activity){
                    is MainActivity ->{
                        activity.tokenUpdateSuccess()
                    }
                    is SettingActivity ->{
                        activity.profileUpdateSuccess()
                    }

                }

            }
            .addOnFailureListener { e ->
                when(activity){
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingActivity ->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }

////    fun createBoard(activity: CreateBoardActivity, board: Board) {
////
////        mfirestore.collection(Constants.BOARDS)
////            .document()
////            .set(board, SetOptions.merge())
////            .addOnSuccessListener {
////                Log.e(activity.javaClass.simpleName, "Board created successfully.")
////
////                Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
////
////                activity.boardCreatedSuccessfully()
////            }
////            .addOnFailureListener { e ->
////                activity.hideProgressDialog()
////                Log.e(
////                    activity.javaClass.simpleName,
////                    "Error while creating a board.",
////                    e
////                )
////            }
////    }
//    fun updatedoctordetails(activity: DoctorDescriptionActivity,doctorHashMap: HashMap<String, Any>,documentId: String) {
//    mfirestore.collection(Constants.DOCTOR) // Collection Name
//        .document(documentId) // Document ID
//        .update(doctorHashMap) // A hashmap of fields which are to be updated.
//        .addOnSuccessListener {
//            // Profile data is updated successfully.
//            Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
//
//            Toast.makeText(activity, "Booking Confirmed", Toast.LENGTH_SHORT).show()
//        }
//    }
//    fun updateuserappointmentdetails(activity: DoctorDescriptionActivity,userHashMap: HashMap<String, Any>,documentId: String) {
//        mfirestore.collection(Constants.USERS) // Collection Name
//            .document(documentId) // Document ID
//            .update(userHashMap) // A hashmap of fields which are to be updated.
//            .addOnSuccessListener {
//                // Profile data is updated successfully.
//                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
//
//                Toast.makeText(activity, "Booking Confirmed", Toast.LENGTH_SHORT).show()
//            }
//    }
@RequiresApi(Build.VERSION_CODES.O)
fun getusersList(activity: MainActivity) {

    // The collection name for DOCTORS
    mfirestore.collection(Constants.DOCTOR)
        .document(getCurrentUserID())
        .get()
        .addOnSuccessListener { document ->
            val loggedInUser = document.toObject(Doctor::class.java)
            var bookedarraylist: ArrayList<Appointment>? = null
            if (loggedInUser != null) {
                bookedarraylist = loggedInUser.appointment
            }
            if (bookedarraylist != null) {
                activity.populatesbookingListToUI(bookedarraylist)
            }

        }
}
    fun getappointmenthistoryList(activity: History, selectedfilter:String) {

        // The collection name for DOCTORS
        val fieldPath = if (selectedfilter == "Successful") {
            "bookingappointment"
        } else {
            "expiredappointment"
        }

        mfirestore.collection(Constants.DOCTOR)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(Doctor::class.java)
                var bookedarraylist: ArrayList<Appointment>? = null
                if (loggedInUser != null) {
                    // Access the field based on the fieldPath
                    bookedarraylist = when (fieldPath) {
                        "bookingappointment" -> loggedInUser.bookingappointment
                        "expiredappointment" -> loggedInUser.expiredappointment
                        else -> null
                    }
                }
                if (bookedarraylist != null) {
                    activity.populatesbookingHistoryListToUI(bookedarraylist)
                }
            }
    }
    fun getslotList(activity: SlotsActivity) {

        // The collection name for DOCTORS
        mfirestore.collection(Constants.DOCTOR)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(Doctor::class.java)
                var timeslotlist: MutableList<String>? = null
                if (loggedInUser != null) {
                    timeslotlist = loggedInUser.timeslots
                }
                if (timeslotlist != null) {
                    activity.loadslotlist(timeslotlist)
                }

            }
    }
    fun updateTimeslot(activity: SlotsActivity, timeslots: MutableList<String>) {
        // Update the 'timeslots' field for the current doctor
        mfirestore.collection(Constants.DOCTOR)
            .document(FirestoreClass().getCurrentUserID())
            .update("timeslots", timeslots)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Timeslots updated successfully.")
                activity.disablebutton()
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error updating timeslots.", e)

            }
    }

//    fun getuserDetails(activity: DoctorDescriptionActivity, documentId: String) {
//        mfirestore.collection(Constants.USERS)
//            .document(documentId)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.toString())
//
//                val board = document.toObject(User::class.java)!!
//
//                // Send the result of board to the base activity.
//                activity.userDetails(board)
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
//            }
//    }
//    fun getuserDetailsinbooking(activity: BookingActivity, documentId: String) {
//        mfirestore.collection(Constants.USERS)
//            .document(documentId)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.toString())
//
//                val board = document.toObject(User::class.java)!!
//
//                // Send the result of board to the base activity.
//                activity.userDetails(board)
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
//            }
//    }
//    fun getdoctorDetails(activity: DoctorDescriptionActivity, documentId: String) {
//        mfirestore.collection(Constants.DOCTOR)
//            .document(documentId)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.toString())
//
//                val board = document.toObject(Doctor::class.java)!!
//
//                // Send the result of board to the base activity.
//                activity.doctorDetails(board)
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
//            }
//    }
//    fun getappointmentDetails(activity: DoctorDescriptionActivity, documentId: String) {
//        mfirestore.collection(Constants.DOCTOR)
//            .document(documentId)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.toString())
//
//                val board = document.toObject(Board::class.java)!!
//                board.documentId = document.id
//
//                // Send the result of board to the base activity.
//                activity.boardDetails(board)
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
//            }
//    }
//    fun addUpdateTaskList(activity: Activity, board: Board) {
//
//        val taskListHashMap = HashMap<String, Any>()
//        taskListHashMap[Constants.TASK_LIST] = board.taskList
//
//        mfirestore.collection(Constants.BOARDS)
//            .document(board.documentId)
//            .update(taskListHashMap)
//            .addOnSuccessListener {
//                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
//                if(activity is  TaskListActivity) {
//                    activity.addUpdateTaskListSuccess()
//                }
//                else if(activity is CardDetailsActivity){
//                    activity.addUpdateTaskListSuccess()
//                }
//            }
//            .addOnFailureListener { e ->
//                if(activity is TaskListActivity) {
//                    activity.hideProgressDialog()
//                }
//                if(activity is CardDetailsActivity) {
//                    activity.hideProgressDialog()
//                }
//                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
//            }
//    }
//    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>) {
//
//        mfirestore.collection(Constants.USERS) // Collection Name
//            .whereIn(Constants.ID, assignedTo) // Here the database field name and the id's of the members.
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.documents.toString())
//
//                val usersList: ArrayList<User> = ArrayList()
//
//                for (i in document.documents) {
//                    // Convert all the document snapshot to the object using the data model class.
//                    val user = i.toObject(User::class.java)!!
//                    usersList.add(user)
//                }
//                if(activity is MembersActivity) {
//                    activity.setupMembersList(usersList)
//                }
//                else if(activity is TaskListActivity){
//                    activity.boardMembersDetailList(usersList)
//                }
//            }
//            .addOnFailureListener { e ->
//                if(activity is MembersActivity) {
//                    activity.hideProgressDialog()
//                }
//                else if(activity is TaskListActivity){
//                    activity.hideProgressDialog()
//                }
//                Log.e(
//                    activity.javaClass.simpleName,
//                    "Error while creating a board.",
//                    e
//                )
//            }
//    }
//    // END
//    fun getMemberDetails(activity: MembersActivity, email: String) {
//
//        // Here we pass the collection name from which we wants the data.
//        mfirestore.collection(Constants.USERS)
//            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
//            .whereEqualTo(Constants.EMAIL, email)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.e(activity.javaClass.simpleName, document.documents.toString())
//
//                if (document.documents.size > 0) {
//                    val user = document.documents[0].toObject(User::class.java)!!
//                    // Here call a function of base activity for transferring the result to it.
//                    activity.memberDetails(user)
//                } else {
//                    activity.hideProgressDialog()
//                    activity.showErrorSnackBar("No such member found.")
//                }
//
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(
//                    activity.javaClass.simpleName,
//                    "Error while getting user details",
//                    e
//                )
//            }
//    }
//    /**
//     * A function to assign a updated members list to board.
//     */
//    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
//
//        val assignedToHashMap = HashMap<String, Any>()
//        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
//
//        mfirestore.collection(Constants.BOARDS)
//            .document(board.documentId)
//            .update(assignedToHashMap)
//            .addOnSuccessListener {
//                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
//                activity.memberAssignSuccess(user)
//            }
//            .addOnFailureListener { e ->
//                activity.hideProgressDialog()
//                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
//            }
//    }
//    // END

    fun getCurrentUserID(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID


    }

}