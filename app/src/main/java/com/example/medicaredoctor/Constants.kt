package com.example.medicaredoctor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap



object Constants {
    const val USERS : String = "users"

    const val  BOARDS : String = "boards"
    const val DOCTOR: String = "doctor"
    const val SPECIALITY: String = "speciality"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val DOCTOR_MODEL: String = "doctor_model"
    const val  APPOINTMENT: String = "appointment"
    const val  USERAPPOINTMENT: String = "userappointment"
    const val TIMING: String = "timing"

    const val ASSIGNED_TO: String = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST :String = "taskList"
    const val BOARD_DETAIL: String = "board_detail"
    const val ID: String =  "id"
    const val EMAIL : String = "email"
    const val BOARDS_MEMBERS_LIST :String = "board_members_list"
    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"
    const val MEDICARE_PREFERENCES = "MedicarePrefs"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAAStFwHNI:APA91bGwKBE3n4xWh5Ak-B8zS2sDy7k2L9FxZqOE5zGR-OylPi6MSq3CNh9cLZvAu6-CkKh8tStcgKpt8Q_0ApwmrpJ6JQW79_rCPXcJdtf13-8RgWSEA5uV9WA507eRNNTqVB6_7yC7"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"
    const val HEIGHT: String = "height"
    const val WEIGHT: String = "weight"
    const val Diabities: String = "diabetic"
    const val AGE: String = "age"
    const val SEND_ID = "SEND_ID"
    const val RECEIVE_ID = "RECEIVE_ID"
    const val OPEN_GOOGLE = "Opening Google..."
    const val OPEN_SEARCH = "Searching..."
    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"

    const val PASSWORD_KEY: String = "Password"
    const val ABOUT :String = "about"
    const val INTEREST: String = "interest"
    const val DEGREE: String = "degree"
    const val HOSPITAL_NAME: String =  "hospital"
    const val ADDRESS : String = "address"
    const val AVAILABLE : String = "available"
    const val GENDER: String = "gender"


    fun showImageChooser(activity: SettingActivity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Context, uri: Uri?): String? {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}