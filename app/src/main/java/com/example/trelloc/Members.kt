package com.example.trelloc

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.Board
import com.example.trelloc.models.Constants
import com.example.trelloc.models.User
import com.projemanag.adapters.MemberListItemsAdapter
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class Members : Baseactivity() {
    private lateinit var mboarddetails:Board
    private lateinit var massignedmemblist:ArrayList<User>
    private var anyChangesmade:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        if (intent.hasExtra(Constants.BOARD_DETAILS)){
            mboarddetails=intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
        }
        setupActionBar()
        Firestore().getAssignedMembersListDetails(this,mboarddetails.assignedTo)
    }
    fun settupmembers(list:ArrayList<User>){
        massignedmemblist=list

        rv_members_list.layoutManager=LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)
        val adapter=MemberListItemsAdapter(this,list)
        rv_members_list.adapter=adapter
    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = resources.getString(R.string.mem)
        }

        toolbar_members_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.menu_add_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_members->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        /*Set the screen content from a layout resource.
    The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener(View.OnClickListener {

            val email = dialog.et_email_search_member.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                Firestore().getmembersDetails(this,email)

            } else {
                Toast.makeText(
                    this@Members,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        dialog.tv_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        //Start the dialog and display it on screen.
        dialog.show()
    }
    fun membersDetails(user: User){
mboarddetails.assignedTo.add(user.id)
        Firestore().assignedmemberstoBoard(this,mboarddetails,user)
    }

    override fun onBackPressed() {


        if (anyChangesmade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }
    fun MembersAssignedSuccess(user: User){
        hideProgressDialog()
        massignedmemblist.add(user)
        anyChangesmade=true
        settupmembers(massignedmemblist)
    }

}