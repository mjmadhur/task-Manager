package com.example.trelloc

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.Constants
import com.example.trelloc.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.util.jar.Manifest

class MyProfile : Baseactivity() {
    private lateinit var mUserDetails: User
    var mselectedimage: Uri? = null
    private var mprofileImageurl: String = ""

    companion object {
        const val READ_EXTERNAL_STORAGE = 1
        const val PICK_IMAGE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setupActionBar()
        Firestore().LoadInUser(this)

        btn_update.setOnClickListener {
            if (mselectedimage != null) {
                UploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateuserProfileData()
            }
        }
        iv_profile_user_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                PickImage()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE
                )
            }
        }



    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }


    fun updateUiwithUser(user:User){
        mUserDetails=user
        Glide.with(this@MyProfile).load(user.image).placeholder(R.drawable.aa).fitCenter().into(iv_profile_user_image)
   et_name.setText(user.name)
        et_email.setText(user.email)
        if (user.mobile!=0L){
et_mobile.setText(user.mobile.toString())
        }

        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== READ_EXTERNAL_STORAGE){
            if (grantResults.isEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
PickImage()
            }

        }
    }
     fun PickImage(){
        val gallIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    if (resultCode== Activity.RESULT_OK &&
            requestCode== PICK_IMAGE_REQUEST_CODE&&
            data!!.data!=null){
        mselectedimage=data.data
        try {
            Glide.with(this@MyProfile).load(mselectedimage).placeholder(R.drawable.aa).into(iv_profile_user_image)

        }catch (e:IOException){
            e.printStackTrace()
        }

    }

    }
    fun profileUpdatesuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    fun getFileExtension(uri:Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))

    }
    private fun UploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mselectedimage!=null){
            val sref:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"
            +System.currentTimeMillis()+"."+getFileExtension(mselectedimage!!))
            sref.putFile(mselectedimage!!).addOnSuccessListener {
                taskSnapshot->
                Log.i("Firebase immage url",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->

                    Log.i("Download uri",uri.toString())
                mprofileImageurl=uri.toString()
                    updateuserProfileData()
                }.addOnFailureListener {
                    exception->
                    Toast.makeText(this@MyProfile,exception.message,Toast.LENGTH_LONG)
                        .show()
                    hideProgressDialog()
                }
            }
        }
    }
   private fun updateuserProfileData(){

        val userhashmap=HashMap<String,Any>()
        if (mprofileImageurl.isNotEmpty()&& mprofileImageurl!=mUserDetails.image){
            userhashmap[Constants.IMAGE]=mprofileImageurl

        }
        if (et_name.text.toString()!=mUserDetails.name){

            userhashmap[Constants.NAME]=et_name.text.toString()
        }
        if (et_mobile.text.toString()!=mUserDetails.mobile.toString()){
            userhashmap[Constants.MOBILE]=et_mobile.text.toString().toLong()

        }

            Firestore().updateuserProfileData(this, userhashmap)
    }
    }
    // END


