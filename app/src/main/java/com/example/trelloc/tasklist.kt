package com.example.trelloc

import android.app.Activity
import android.content.Intent
import android.icu.text.CaseMap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.*
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.adapters.TaskListItemsAdapter
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_tasklist.*

class tasklist : Baseactivity() {
    private lateinit var mboarddetails:Board
    private lateinit var boardid:String
     lateinit var mAssignedMemDetails:ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasklist)

        if (intent.hasExtra(Constants.DOCUMENTID)){
boardid= intent.getStringExtra(Constants.DOCUMENTID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))

        Firestore().getBoardDetails(this,boardid)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == MEMBERS_REQUEST_CODE || requestCode== CARD_DETAILS_REQUEST_CODE
        ) {
            // Show the progress dialog.
showProgressDialog(resources.getString(R.string.please_wait))
            Firestore().getBoardDetails(this@tasklist, boardid)
            //hideProgressDialog()
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.action_members->{
               val intent=Intent(this,Members::class.java)
               intent.putExtra(Constants.BOARD_DETAILS,mboarddetails)
               startActivityForResult(intent, MEMBERS_REQUEST_CODE)
               return true
           }
           R.id.Sign_Out->{
               FirebaseAuth.getInstance().signOut()
               val intent= Intent(this,Intro::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
               startActivity(intent)

               finish()
           }
           R.id.Edit_Profile->{
               startActivity(Intent(this,MyProfile::class.java))
           }
       }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title = mboarddetails.name
        }

        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
    fun boardetails(board: Board){

        mboarddetails=board
        hideProgressDialog()
        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getAssignedMembersListDetails(this,mboarddetails.assignedTo)
    }
    fun updateTaskListSuccess(){
hideProgressDialog()
        //showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getBoardDetails(this,mboarddetails.documentId)

    }
    fun createtaskList(tasklistname:String){
val task=Task(tasklistname,Firestore().getcurrentuserid())
        mboarddetails.tasklist.add(0,task)
        mboarddetails.tasklist.removeAt(mboarddetails.tasklist.size-1)
        Firestore().addUpdateTaskList(this,mboarddetails)
        showProgressDialog(resources.getString(R.string.please_wait))

    }
    fun UpdateTaskList(position:Int,listName:String,model:Task){
        val task=Task(listName,model.createdBy)
        mboarddetails.tasklist[position]=task

        mboarddetails.tasklist.removeAt(mboarddetails.tasklist.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
    Firestore().addUpdateTaskList(this,mboarddetails)
    }
    fun deleteTask(position: Int){
        mboarddetails.tasklist.removeAt(position)
        mboarddetails.tasklist.removeAt(mboarddetails.tasklist.size-1)
        showProgressDialog(resources.getString(R.string.please_wait)
        )
        Firestore().addUpdateTaskList(this,mboarddetails)
    }
    fun addCardToList(position: Int,cardname:String){
        mboarddetails.tasklist.removeAt(mboarddetails.tasklist.size-1)
        val cardAssignedUsersList:ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(Firestore().getcurrentuserid())
        val card=Card(cardname,Firestore().getcurrentuserid(),cardAssignedUsersList)
        val cardList=mboarddetails.tasklist[position].cards
        cardList.add(card)
        val task=Task(
            mboarddetails.tasklist[position].title,
            mboarddetails.tasklist[position].createdBy,
            cardList
        )
            mboarddetails.tasklist[position]=task
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().addUpdateTaskList(this,mboarddetails)

    }
    companion object{
        const val MEMBERS_REQUEST_CODE:Int=13
        const val CARD_DETAILS_REQUEST_CODE:Int=14
    }
    fun cardDetails(listposition:Int,cardposition:Int){
        val intent=Intent(this,CardDetails::class.java)
        intent.putExtra(Constants.BOARD_DETAILS,mboarddetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,listposition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardposition)
        intent.putExtra(Constants.BOARD_MEMBERS_List,mAssignedMemDetails)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }
    fun BoardMemberDetails(list:ArrayList<User>){
        mAssignedMemDetails=list
        hideProgressDialog()
        val addTaskList = Task(resources.getString(R.string.add_list))
        mboarddetails.tasklist.add(addTaskList)

        rv_task_list.layoutManager =
            LinearLayoutManager(this@tasklist, LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        // Create an instance of TaskListItemsAdapter and pass the task list to it.
        val adapter = TaskListItemsAdapter(this@tasklist, mboarddetails.tasklist)
        rv_task_list.adapter = adapter

    }
    fun updateCardsInTaskList(tasklistPosition: Int, cards: ArrayList<Card>) {

        mboarddetails.tasklist.removeAt(mboarddetails.tasklist.size-1)

        mboarddetails.tasklist[tasklistPosition].cards = cards

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().addUpdateTaskList(this@tasklist, mboarddetails)
    }

}