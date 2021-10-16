package com.example.trelloc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloc.adapter.boarditemAdapter
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.Board
import com.example.trelloc.models.Constants
import com.example.trelloc.models.SwipetoDelete
import com.example.trelloc.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : Baseactivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
     const val CREATE_BOARD_REQUEST_CODE=12
        const val MY_PROFILE_REQUEST_CODE:Int=11
    }

     private lateinit var mUsername:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
setSupportActionBar(toolbar_main_activity)
toolbar_main_activity.title="Boards"
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_baseline_menu_24)
toolbar_main_activity.setNavigationOnClickListener {
toggleDrawer()
}
nv_main.setNavigationItemSelectedListener(this)
        Firestore().LoadInUser(this@MainActivity,true)

        fab_create_board.setOnClickListener {
            val intent=Intent(this@MainActivity,CreateBoard::class.java)
            intent.putExtra(Constants.NAME, mUsername)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }
    private fun toggleDrawer(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()

        }

    }
    fun updateNavigationuser(user:User,readboardslist:Boolean) {
        mUsername = user.name



        Glide.with(this).load(user.image)
            .placeholder(R.drawable.ic_baseline_supervised_user_circle_24).into(iv_user_image)
        tv_username.text = user.name
        if (readboardslist) {
            showProgressDialog(resources.getString(R.string.please_wait))
            Firestore().getBoardsList(this)

        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                @Suppress("DEPRECATION")
                startActivityForResult(Intent(this,MyProfile::class.java), MY_PROFILE_REQUEST_CODE)
                


            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,Intro::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
           startActivity(intent)
                finish()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

if (resultCode== Activity.RESULT_OK&& requestCode== MY_PROFILE_REQUEST_CODE){
    Firestore().LoadInUser(this)
}else if (resultCode==Activity.RESULT_OK&& requestCode== CREATE_BOARD_REQUEST_CODE){
    Firestore().getBoardsList(this)
}
else{
    Log.e("Cancel","Cancelled...!!!")
}
    }
     fun populateBoard(boardslist:ArrayList<Board>){

        if (boardslist.size>0){
            rv_boards_list.visibility=View.VISIBLE
            tv_no_boards_available.visibility=View.GONE
            rv_boards_list.layoutManager=LinearLayoutManager(this@MainActivity)
            rv_boards_list.setHasFixedSize(true)
            val adapter=boarditemAdapter(this,boardslist)
            rv_boards_list.adapter=adapter
            adapter.setOnClickListener(object :boarditemAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    showErrorSnackBar("Please Wait For The Detailed Boards To Load From Internet")
                    Handler().postDelayed({

                        val intent = Intent(this@MainActivity, tasklist::class.java)
                        intent.putExtra(Constants.DOCUMENTID, model.documentId)
                        startActivity(intent)
                    },1000)
                }

            })
            val deleteSwipeHadler = object : SwipetoDelete(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                   showProgressDialog("please Wait")

Firestore().deleteBoard(this@MainActivity,boardslist[viewHolder.adapterPosition].documentId)


                }

            }
            val deleteitemtouchHelper = ItemTouchHelper(deleteSwipeHadler)
            deleteitemtouchHelper.attachToRecyclerView(rv_boards_list)
            hideProgressDialog()
        }else{
            rv_boards_list.visibility=View.GONE
            tv_no_boards_available.visibility=View.VISIBLE
      }

       //  Firestore().getBoardsList(this@MainActivity)
     }
    fun deleteboardsuccess(){
        hideProgressDialog()
        showErrorSnackBar("delete successfull")
      getboards()

    }
    fun getboards(){
        showProgressDialog("Wait")
        Firestore().getBoardsList(this)
    }
      }



