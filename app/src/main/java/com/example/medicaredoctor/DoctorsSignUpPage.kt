package com.example.medicaredoctor

//import com.example.medicaredoctor.DoctorsSignUpPage.TwilioConstants.ACCOUNT_SID
//import com.example.medicaredoctor.Interface.createTwilioApiService
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.medicaredoctor.DoctorsSignUpPage.TwilioConstants.ACCOUNT_SID
import com.example.medicaredoctor.Interface.createTwilioApiService
import com.example.medicaredoctor.databinding.ActivityDoctorsSignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random


class DoctorsSignUpPage : BaseActivity() {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var binding: ActivityDoctorsSignUpPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var attachedPdfUri: Uri? = null
    val storage = FirebaseStorage.getInstance()
    val storageReference = storage.reference
    object TwilioConstants {
        const val ACCOUNT_SID = "ACade57ae65880ce3b2511b46976c6e3f0"
        const val AUTH_TOKEN = "1bbf43a7e99968e916acf6e669024360"
        const val FROM_PHONE_NUMBER = "+14043345873"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsSignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signin.setOnClickListener {
            startActivity(Intent(this, DoctorsLoginPage::class.java))
        }
        firebaseAuth = FirebaseAuth.getInstance()
        binding.signup.setOnClickListener {
            val mobileNumber = binding.phone.text.toString()
            val fullName = binding.name.text.toString()
            val email = binding.email.text.toString()


            if (fullName.isEmpty()) {
                Toast.makeText(this, "Enter you name", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
//            } else if (password.isEmpty() || confirmPassword.isEmpty()) {
//                Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show()
//            } else if (password != confirmPassword) {
//                Toast.makeText(this, "Password not matching", Toast.LENGTH_LONG).show()
            } else if (mobileNumber.isEmpty() || mobileNumber.length < 10) {
                Toast.makeText(this, "Please enter Valid Mobile Number", Toast.LENGTH_SHORT).show()
            }
            else if(attachedPdfUri==null){
                Toast.makeText(this, "Please attach File", Toast.LENGTH_SHORT).show()

            }
            else {
                showProgressDialog("Please Wait")
                addDatatoFirebase(fullName, email, mobileNumber,"${fullName}_${generateRandomString(5)}")
            }
        }
        binding.attachButton.setOnClickListener {
            pickPdfFile()
        }
        binding?.cancelButton?.setOnClickListener {
            cancelAttachment()
        }

    }

//    private fun signup(
//        fullName: String,
//        email: String,
//        mobileNumber: String
//    ) {
////        firebaseAuth.createUserWithEmailAndPassword(email, password)
////            .addOnCompleteListener(this) { task ->
////                if (task.isSuccessful) {
////                    startActivity(Intent(this, DoctorsLoginPage::class.java))
////                    Toast.makeText(this, "Signup Successfully", Toast.LENGTH_SHORT).show()
////                    val twilioApiServiceUser = createTwilioApiService()
////                    val toPhoneNumberUser = "+91$mobileNumber"
////                    val fromPhoneNumberUser =
////                        "+14043345873" // Replace with your Twilio phone number
////                    val messageUser = "Hey Your id is $doctorsId"
//
//                    GlobalScope.launch(Dispatchers.IO) {
//                        try {
//                            val response = twilioApiServiceUser.sendSMS(
//                                ACCOUNT_SID,
//                                toPhoneNumberUser,
//                                fromPhoneNumberUser,
//                                messageUser
//                            ).execute()
//                            if (response.isSuccessful) {
//                                // SMS sent successfully
//                            } else {
//                                // Handle the failure case here
//                            }
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                            // Handle the exception here
//                        }
//                    }
//
////                    addDatatoFirebase(
////                        fullName,
////                        email,
////                        password,
////                        firebaseAuth.currentUser?.uid!!,
////                        doctorsId
////                    )
//                } else {
//                    val errorMessage = when (task.exception) {
//                        is FirebaseAuthWeakPasswordException -> "Weak password. Password should be at least 8 characters."
//                        is FirebaseAuthInvalidCredentialsException -> "Invalid email address."
//                        is FirebaseAuthUserCollisionException -> "This email is already registered."
//                        else -> "Signup failed. Please try again."
//                    }
//                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
//                }
//            }
    fun pickPdfFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            attachedPdfUri = data?.data // Store the selected PDF URI
            // Update the UI to show the attached PDF's name and the cancel icon
            val pdfFileName = getFileNameFromUri(attachedPdfUri)
            binding.attachedPdfTextView.text = pdfFileName
            binding.cancelButton.visibility = View.VISIBLE
        }
    }

    // Function to get the file name from a URI
    private fun getFileNameFromUri(uri: Uri?): String {
        var result: String? = null
        if (uri != null) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                result = cursor.getString(nameIndex)
            }
        }
        return result ?: "Unknown File"
    }


    fun cancelAttachment() {
        attachedPdfUri = null // Clear the attached PDF URI
        // Update the UI to indicate no file attached
        binding.attachedPdfTextView.text = "No file attached"
        binding.cancelButton.visibility = View.GONE
    }

    private fun addDatatoFirebase(
        fullName: String,
        email: String,
        mobile: String,
        doctorsId: String
    ) {

        val storageRef = storageReference.child("doctor_pdfs/$doctorsId.pdf") // Set the desired path for the PDF file

        if (attachedPdfUri != null) {
            val uploadTask = storageRef.putFile(attachedPdfUri!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                hideProgressDialog()
                Toast.makeText(this, "PDF uploaded successfully", Toast.LENGTH_SHORT).show()
                val twilioApiServiceUser = createTwilioApiService()
                    val toPhoneNumberUser = "+91$mobile"
                    val fromPhoneNumberUser =
                        "+14043345873" // Replace with your Twilio phone number
                    val messageUser = "Hey Your id is $doctorsId"

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val response = twilioApiServiceUser.sendSMS(
                                ACCOUNT_SID,
                                toPhoneNumberUser,
                                fromPhoneNumberUser,
                                messageUser
                            ).execute()
                            if (response.isSuccessful) {
                                // SMS sent successfully
                            } else {
                                // Handle the failure case here
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            // Handle the exception here
                        }
                    }

                val twilioApiServiceUser2 = createTwilioApiService()
                val toPhoneNumberUser2 = "+918318029261"
                val fromPhoneNumberUser2 =
                    "+14043345873" // Replace with your Twilio phone number
                val messageUser2 = "Hey Your id is $doctorsId"

                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val response = twilioApiServiceUser2.sendSMS(
                            ACCOUNT_SID,
                            toPhoneNumberUser2,
                            fromPhoneNumberUser2,
                            messageUser2
                        ).execute()
                        if (response.isSuccessful) {
                            // SMS sent successfully
                        } else {
                            // Handle the failure case here
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Handle the exception here
                    }
                }
                startActivity(Intent(this@DoctorsSignUpPage, DoctorsLoginPage::class.java))
                finish()


                // You can get the download URL for the uploaded file if needed
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Save the downloadUrl in the database or perform any other actions
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "PDF upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // No PDF attached
            Toast.makeText(this, "No PDF attached", Toast.LENGTH_SHORT).show()
        }


    }
//        databaseReference = FirebaseDatabase.getInstance().reference
//        databaseReference.child("Doctor").child(doctorsId)
//            .setValue(Doctors(fullName, email, password, uid, doctorsId))



    private fun generateRandomString(length: Int): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars[Random.nextInt(0, allowedChars.length)] }
            .joinToString("")
    }
}