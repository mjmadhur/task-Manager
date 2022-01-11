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
import com.example.trelloc.models.Board
import com.example.trelloc.models.Constants
import com.google.common.io.Files.getFileExtension
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException
import kotlin.concurrent.timerTask

class CreateBoard :Baseactivity() {

    private var mselectedimage:Uri?=null
    private lateinit var mUsername:String
    private var mBoardImage:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setupActionBar()
        if (intent.hasExtra(Constants.NAME)){
            mUsername= intent.getStringExtra(Constants.NAME)!!
        }
        btn_create.setOnClickListener {
            if (mselectedimage!=null){
                UploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
               // FirebaseServ().sendNotification("hey ${mUsername} created a board" )
            }
        }

    }
    fun boardcreatedSuccess(){
        val bn=et_board_name.text.toString()
        FirebaseServ().sendNotification("Hey ${bn} Created By ${mUsername}")
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title="Create Project"
        }

        toolbar_create_board_activity.setNavigationOnClickListener { onBackPressed() }
        iv_board_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_GRANTED){
                PickImage()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    MyProfile.READ_EXTERNAL_STORAGE
                )
            }
        }
    }
    fun PickImage(){
        val gallIntent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(gallIntent, MyProfile.PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(uri:Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK &&
            requestCode== MyProfile.PICK_IMAGE_REQUEST_CODE &&
            data!!.data!=null){
            mselectedimage=data.data
            try {
                Glide.with(this).load(mselectedimage).placeholder(R.drawable.ic_baseline_supervised_user_circle_24).into(iv_board_image)

            }catch (e: IOException){
                e.printStackTrace()
            }

        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== MyProfile.READ_EXTERNAL_STORAGE){
            if (grantResults.isEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                PickImage()
            }

        }
    }
    private fun createBoard(){
        val assignedUsersList:ArrayList<String> =ArrayList()
        assignedUsersList.add(getCurrentUserID())
        var board=Board(
            et_board_name.text.toString(),
            mBoardImage,
            mUsername,
            assignedUsersList


        )
        Firestore().createBoard(this,board)
    }
    private fun UploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mselectedimage!=null){
            val sref: StorageReference = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"
                    +System.currentTimeMillis()+"."+getFileExtension(mselectedimage!!))
            sref.putFile(mselectedimage!!).addOnSuccessListener {
                    taskSnapshot->
                Log.i("Firebase immage url",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->

                    Log.i("Download uri",uri.toString())
                    mBoardImage=uri.toString()
                    createBoard()
                }.addOnFailureListener {
                        exception->
                    Toast.makeText(this,exception.message, Toast.LENGTH_LONG)
                        .show()
                    hideProgressDialog()
                }
            }
        }
    }

}