package com.example.trelloc

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trelloc.adapter.CardMembersListAdapter
import com.example.trelloc.dialog.LabelcolorListDialog
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.*
import com.projemanag.dialogs.MembersListDialog
import com.projemanag.model.SelectedMembers
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_members.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetails : Baseactivity() {
    private lateinit var mboardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor: String = ""
    private lateinit var mMembersList: ArrayList<User>
    private var mselecteddateinms:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getintentdata()
        setupActionBar()
        et_name_card_details.setText(mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)
        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()) {
                UppdateCardDetails()
            } else {
                showErrorSnackBar("Enter A Card Name")
            }
        }
        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }
        mSelectedColor = mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }
        tv_select_members.setOnClickListener {
            membersListDialog()
        }
        setupSelectedMembersList()
        tv_select_due_date.setOnClickListener {

            showDataPicker()
        }
        mselecteddateinms=mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].duedate
        if (mselecteddateinms > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mselecteddateinms))
            tv_select_due_date.text = selectedDate
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_cards -> {
                alertDialogForDeleteCard(mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            //actionBar.title = resources.getString(R.string.action_add_card)
            actionBar.title = mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].name
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getintentdata() {
        if (intent.hasExtra(Constants.BOARD_DETAILS)) {
            mboardDetails = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_List)) {
            mMembersList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_List)!!
        }


    }

    fun addupdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun UppdateCardDetails() {
        val card = Card(
            et_name_card_details.text.toString(),
            mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].createdBy,
            mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mselecteddateinms
        )
       // val tasklist: ArrayList<Task> = mboardDetails.tasklist
        //tasklist.removeAt(tasklist.size- 1)
        val tasklist:ArrayList<Task> = mboardDetails.tasklist
        tasklist.removeAt(tasklist.size - 1)
        mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition] = card
        showProgressDialog(
            resources.getString(R.string.please_wait)
        )
        Firestore().addUpdateTaskList(this@CardDetails, mboardDetails)


    }

    private fun deleteCard() {


        val cardsList: ArrayList<Card> = mboardDetails.tasklist[mTaskListPosition].cards

        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mboardDetails.tasklist
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().addUpdateTaskList(this@CardDetails, mboardDetails)
    }

    private fun alertDialogForDeleteCard(cardname: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,
                cardname
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            deleteCard()

        }

        builder.setNegativeButton("NO") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun colorsList(): ArrayList<String> {

        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun labelColorsListDialog() {

        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelcolorListDialog(
            this@CardDetails,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun membersListDialog() {

        // Here we get the updated assigned members list
        val cardAssignedMembersList =
            mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {

            for (i in mMembersList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersList[i].id == j) {
                        mMembersList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersList.indices) {
                mMembersList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this@CardDetails,
            mMembersList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {


                if (action == Constants.SELECT) {
                    if (!mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].assignedTo.contains(
                            user.id
                        )
                    ) {
                        mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].assignedTo.add(
                            user.id
                        )
                    }
                } else {
                    mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].assignedTo.remove(
                        user.id
                    )

                    for (i in mMembersList.indices) {
                        if (mMembersList[i].id == user.id) {
                            mMembersList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()
                // END
            }
        }
        listDialog.show()
    }



    private fun setupSelectedMembersList() {


        val cardAssignedMembersList =
            mboardDetails.tasklist[mTaskListPosition].cards[mCardPosition].assignedTo

        // A instance of selected members list.
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()


        for (i in mMembersList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersList[i].id,
                        mMembersList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {

            // This is for the last item to show.
            selectedMembersList.add(SelectedMembers("", ""))

            tv_select_members.visibility = View.GONE
            rv_selected_members_list.visibility = View.VISIBLE

            rv_selected_members_list.layoutManager = GridLayoutManager(this@CardDetails, 6)
            val adapter = CardMembersListAdapter(this@CardDetails, selectedMembersList,true)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(object :
                CardMembersListAdapter.OnClickListener {
                override fun onClick() {
                    membersListDialog()
                }
            })
        } else {
            tv_select_members.visibility = View.VISIBLE
            rv_selected_members_list.visibility = View.GONE
        }
    }
    private fun showDataPicker() {

        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                // Selected date it set to the TextView to make it visible to user.
                tv_select_due_date.text = selectedDate


                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)

                /** Here we have get the time in milliSeconds from Date object
                 */

                /** Here we have get the time in milliSeconds from Date object
                 */
                mselecteddateinms = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }


}