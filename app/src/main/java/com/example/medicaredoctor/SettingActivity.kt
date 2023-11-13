package com.example.medicaredoctor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.medicaredoctor.Models.Doctor


import com.example.medicaredoctor.databinding.ActivitySettingBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class SettingActivity : AppCompatActivity() {
    private var binding: ActivitySettingBinding? =null
    private lateinit var mProgressDialog: Dialog

    private var mSelectedImageFileUri: Uri? = null

    private var mProfileImageURL: String = ""
    private var avaialble:Boolean? = null

    private lateinit var mDoctorDetails: Doctor
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()
        showProgressDialog("Please Wait")
        FirestoreClass().loadUserData(this)


        findViewById<CircleImageView>(R.id.iv_profile_user_image)?.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this,)

            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        findViewById<Button>(R.id.btn_update)?.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileData()
            }
        }


    }
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        val tvProgressText: TextView = mProgressDialog.findViewById(R.id.tv_progress_text)
        tvProgressText.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }
     fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    override fun onBackPressed() {
        super.onBackPressed()

        // Navigate to MainActivity with the home menu item selected
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedItemId", R.id.home) // Pass the ID of the home menu item
        startActivity(intent)
        finish()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode ==   Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(
                this,
                "Oops, you just denied the permission for storage. You can also allow it from settings.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                val iv_profile_user_image = findViewById<CircleImageView>(R.id.iv_profile_user_image)
                Glide
                    .with(this@SettingActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(iv_profile_user_image) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    // END
    private fun setupActionBar(){

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        if(actionbar !=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionbar.title = resources.getString(R.string.my_profile)
        }
        toolbar.setNavigationOnClickListener{
            onBackPressed()
        }



    }


    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this,
                    mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@SettingActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }


    // END
    fun profileUpdateSuccess() {


        setResult(Activity.RESULT_OK)

        finish()
    }
    @SuppressLint("SuspiciousIndentation")
    fun setUserDataInUI(user: Doctor) {
        hideProgressDialog()
        mDoctorDetails = user

        val iv_user_image = findViewById<CircleImageView>(R.id.iv_profile_user_image)
        Glide
            .with(this@SettingActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)

        val et_name = findViewById<TextView>(R.id.et_name)
        et_name.setText(user.name)

        val et_email = findViewById<TextView>(R.id.et_email)
        et_email.setText(user.email)
        et_email.isEnabled = false

        val et_mobile = findViewById<TextView>(R.id.et_mobile)
        if (user.mobile != 0L) {
            et_mobile.setText(user.mobile.toString())
        }

        val et_degree = findViewById<TextView>(R.id.et_degree)
        et_degree.setText(user.degree.toString())
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)

            if (user.available==true) {
                toggleButton.isChecked = true

            } else {

                toggleButton.isChecked = false
            }



        val et_interest = findViewById<EditText>(R.id.et_Interest)

        et_interest.setText(user.interest.toString())

        val et_about = findViewById<EditText>(R.id.et_about)
        et_about.setText(user.about.toString())
        val et_hospital = findViewById<EditText>(R.id.et_Hospital)

        et_hospital.setText(user.hospital.toString())

        val et_address = findViewById<EditText>(R.id.et_Address)
        et_address.setText(user.address.toString())
        val et_speciality = findViewById<EditText>(R.id.et_specilaity)
        et_speciality.setText(user.speciality.toString())



        val gender_spinner = findViewById<Spinner>(R.id.spinner_gender)

        val stringList = listOf("Male", "Female", "Transgender")

        val position = stringList.indexOf(user.gender)

            // String found in the list, position will be the index of the string
            // Set the selected item in the Spinner based on the position
            gender_spinner.setSelection(position)


// Disable the spinner to prevent user interaction
        gender_spinner.isEnabled = false

//        val blood_spinner = findViewById<Spinner>(R.id.spinner_blood)
//
//        val stringList2 = listOf("A+", "B+", "AB+", "O+", "A-", "B-", "O-", "AB-")
//
//        val position2 = stringList2.indexOf(user.bloodgroup)

        // String found in the list, position will be the index of the string
//        // Set the selected item in the Spinner based on the position
//        blood_spinner.setSelection(position2)
//
//
//// Disable the spinner to prevent user interaction
//        blood_spinner.isEnabled = false
////
//        val diabetic_spinner = findViewById<Spinner>(R.id.diabites)

        val stringList3 = listOf("No", "Yes")

//        val position3 = stringList3.indexOf(user.diabetic)

        // String found in the list, position will be the index of the string
        // Set the selected item in the Spinner based on the position
//        diabetic_spinner.setSelection(position3)


// Disable the spinner to prevent user interaction




    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mDoctorDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        val et_name = findViewById<EditText>(R.id.et_name)
        val et_mobile = findViewById<EditText>(R.id.et_mobile)
        val et_about = findViewById<EditText>(R.id.et_about)
        val et_interest = findViewById<EditText>(R.id.et_Interest)
        val et_degree = findViewById<EditText>(R.id.et_degree)
        val et_hospital_name = findViewById<EditText>(R.id.et_Hospital)
        val et_address = findViewById<EditText>(R.id.et_Address)
        val et_speciality = findViewById<EditText>(R.id.et_specilaity)
        val toggleButton = findViewById<ToggleButton>(R.id.toggleButton)


//        val et_diabetic = findViewById<Spinner>(R.id.diabites)

        if (et_name.text.toString() != mDoctorDetails.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()
        }

        if (et_mobile.text.toString() != mDoctorDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
        }

        if (et_about.text.toString() != mDoctorDetails.about.toString()) {
            userHashMap[Constants.ABOUT] = et_about.text.toString()
        }

        if (et_interest.text.toString() != mDoctorDetails.interest.toString()) {
            userHashMap[Constants.INTEREST] = et_interest.text.toString()
        }


        if (et_hospital_name.text.toString() != mDoctorDetails.hospital.toString()) {
            userHashMap[Constants.HOSPITAL_NAME] = et_hospital_name.text.toString()
        }
        if (et_address.text.toString() != mDoctorDetails.address.toString()) {
            userHashMap[Constants.ADDRESS] = et_address.text.toString()
        }
        if (et_degree.text.toString() != mDoctorDetails.degree.toString()) {
            userHashMap[Constants.DEGREE] = et_degree.text.toString()
        }
        if (et_speciality.text.toString() != mDoctorDetails.speciality.toString()) {
            userHashMap[Constants.SPECIALITY] = et_speciality.text.toString()
        }

        avaialble = toggleButton.isChecked
        if (avaialble!= mDoctorDetails.available) {
            userHashMap[Constants.AVAILABLE] = avaialble!!
        }








        FirestoreClass().updateUserProfileData(this, userHashMap)
    }


}