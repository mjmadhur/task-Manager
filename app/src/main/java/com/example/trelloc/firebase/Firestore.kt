package com.example.trelloc.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.trelloc.*
import com.example.trelloc.models.Board
import com.example.trelloc.models.Constants
import com.example.trelloc.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.FieldPosition
import kotlin.math.log

class Firestore {
    private val firestore=FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUp, userinfo:User){
        firestore.collection(Constants.USERS).document(
            getcurrentuserid()).set(userinfo, SetOptions.merge()).addOnSuccessListener {
                activity.useregisteredSuccess()
        }


    }
    fun LoadInUser(activity:Activity,readboardslist:Boolean=true){
        firestore.collection(Constants.USERS).document(
            getcurrentuserid()).get().addOnSuccessListener{document->
            val loggedinUser=document.toObject(User::class.java)!!
            when(activity){
                is SignIn->{
                    activity.signedInSuccess(loggedinUser)
                }
                is MainActivity->{
                    activity.updateNavigationuser(loggedinUser,readboardslist)
                }
                is MyProfile->{
                    activity.updateUiwithUser(loggedinUser)
                }
            }
        }.addOnFailureListener {
            e->
            Log.e("SIGN IN","FAILED TO SIGN IN",e)
        }

    }

     fun getcurrentuserid():String{
        val currentuser=FirebaseAuth.getInstance().currentUser
        var Currentuserid=""
        if (currentuser!=null){
            Currentuserid=currentuser.uid
        }
        return Currentuserid
    }
   fun updateuserProfileData(activity:Activity,userhashmap:HashMap<String,Any>){
        firestore.collection(Constants.USERS).document(
            getcurrentuserid()).update(userhashmap).addOnSuccessListener {

            Toast.makeText(activity,"Profile Updated Successfully",Toast.LENGTH_LONG).show()
            when(activity ){
                is MyProfile-> {
                    activity.profileUpdatesuccess()
                }
                is MainActivity->{
                    activity.tokenUpdatesucess()
                }
            }
        }.addOnFailureListener {
            when(activity) {
is MyProfile->
                activity.hideProgressDialog()
            }
        }
    }
    fun createBoard(activity:CreateBoard,board:Board){

        firestore.collection(Constants.BOARDS).document()
            .set(board, SetOptions.merge()).addOnSuccessListener {

                Toast.makeText(activity,"Board Created Success",Toast.LENGTH_LONG).show()
                activity.boardcreatedSuccess()


            }.addOnFailureListener {
                exception->
                activity.hideProgressDialog()
            }
    }
    fun deleteBoard(activity: MainActivity,documentid:String){
        firestore.collection(Constants.BOARDS).document(documentid).delete().addOnSuccessListener {
activity.deleteboardsuccess()
        }
    }

    fun getBoardsList(activity: MainActivity){ firestore
        .collection(Constants.BOARDS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereArrayContains(Constants.ASSIGNED_TO, getcurrentuserid())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for Boards ArrayList.
                val boardsList: ArrayList<Board> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id

                    boardsList.add(board)
                }

                // Here pass the result to the base activity.
                activity.populateBoard(boardsList)
                activity.hideProgressDialog()
            }
            .addOnFailureListener {
                 e ->

                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)


            }
    }

    fun getBoardDetails(activity:tasklist,documentid:String) {
        firestore
            .collection(Constants.BOARDS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .document(documentid)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.toString())
                val board=document.toObject(Board::class.java)!!
                board.documentId=document.id
                // Here we have created a new instance for Boards ArrayList.
activity.boardetails(board)



            }
            .addOnFailureListener {
                    e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)


            }


    }
    fun addUpdateTaskList(activity: Activity, board: Board) {

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.tasklist

      firestore.collection(Constants.BOARDS).document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
if (activity is tasklist){
                activity.updateTaskListSuccess()}
                else if (activity is CardDetails){
    activity.addupdateTaskListSuccess()}
            }
            .addOnFailureListener { e ->
if (activity is tasklist){
    activity.hideProgressDialog()}
 else if (activity is CardDetails){
     activity.hideProgressDialog()}
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }
    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>) {

        firestore.collection(Constants.USERS) // Collection Name
            .whereIn(Constants.ID, assignedTo) // Here the database field name and the id's of the members.
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()

                for (i in document.documents) {
                    // Convert all the document snapshot to the object using the data model class.
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }
if (activity is Members){
                activity.settupmembers(usersList)}
                else if (activity is tasklist){
                    activity.BoardMemberDetails(usersList)}
            }
            .addOnFailureListener { e ->
                if (activity is Members)
                activity.hideProgressDialog()
                else if (activity is tasklist )
                    activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
            }
    }
    fun getmembersDetails(activity: Members,email:String){
        firestore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL,email)
            .get().addOnSuccessListener {
                document->
                if (document.documents.size>0){
                    val user=document.documents[0].toObject(User::class.java)!!
                    activity.membersDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No Such Members Found")
                }
            }.addOnFailureListener {
                activity.hideProgressDialog()
                activity.showErrorSnackBar("Failed")
            }
    }
    fun assignedmemberstoBoard(activity: Members,board: Board,user: User){
        val AssignedToHashmap=HashMap<String,Any>()
        AssignedToHashmap[Constants.ASSIGNED_TO]=board.assignedTo
        firestore.collection(Constants.BOARDS).document(board.documentId)
            .update(AssignedToHashmap).addOnSuccessListener {
                activity.MembersAssignedSuccess(user)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Errorrrrr!!")
            }
    }




}