package com.projemanag.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloc.R
import com.example.trelloc.adapter.cardListAdapter

import com.example.trelloc.models.Task
import com.example.trelloc.tasklist
import kotlinx.android.synthetic.main.item_task.view.*
import java.util.*
import kotlin.collections.ArrayList


open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>,


) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mdraggedfrom=-1
    private var mdraggedto=-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE
            } else {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }
            holder.itemView.tv_task_list_title.text=model.title
            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE

            }
            holder.itemView.ib_close_list_name.setOnClickListener{
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE

            }
            holder.itemView.ib_done_list_name.setOnClickListener {
                val listname = holder.itemView.et_task_list_name.text.toString()
                if (listname.isNotEmpty()) {
                    if (context is tasklist) {
                        context.createtaskList(listname)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_LONG).show()

                }
            }
                holder.itemView.ib_edit_list_name.setOnClickListener {
                    val listname = holder.itemView.et_task_list_name.text.toString()
                    val builder = AlertDialog.Builder(context)
                    //set title for alert dialog
                    builder.setTitle("Alert")
                    builder.setMessage("Are you sure you want to edit $listname.It will Delete Your Cards Create A new One If changes Are required")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Yes"){ dialogInterface,which->
                        dialogInterface.dismiss()

                    }
                    builder.setNegativeButton("No") { dialogInterface, which ->
                        holder.itemView.ll_title_view.visibility=View.VISIBLE
                        holder.itemView.cv_edit_task_list_name.visibility=View.GONE
                        dialogInterface.dismiss() // Dialog will be dismissed
                    }
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
                    alertDialog.show()





                    Toast.makeText(context,"Editing List Name Will Result in Loss of cards!! ",Toast.LENGTH_LONG).show()
                    holder.itemView.et_edit_task_list_name.setText(model.title)
                    holder.itemView.ll_title_view.visibility=View.GONE
                    holder.itemView.cv_edit_task_list_name.visibility=View.VISIBLE
                }
                holder.itemView.ib_close_editable_view.setOnClickListener{
                    holder.itemView.ll_title_view.visibility = View.VISIBLE
                    holder.itemView.cv_edit_task_list_name.visibility = View.GONE

                }
                holder.itemView.ib_done_edit_list_name.setOnClickListener {

                    val listname=holder.itemView.et_edit_task_list_name.text.toString()

                    if (listname.isNotEmpty() ){
                        if (context is tasklist){
                            context.UpdateTaskList(position,listname,model)

                        }
                    }else{
                        Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_LONG).show()

                    }

                }
            holder.itemView.ib_delete_list.setOnClickListener {

                alertDialogForDeleteList(position, model.title)
            }
holder.itemView.tv_add_card.setOnClickListener {
    holder.itemView.tv_add_card.visibility=View.GONE
    holder.itemView.cv_add_card.visibility=View.VISIBLE
}
            holder.itemView.ib_close_card_name.setOnClickListener{
                holder.itemView.tv_add_card.visibility=View.VISIBLE
                holder.itemView.cv_add_card.visibility=View.GONE

            }
            holder.itemView.ib_done_card_name.setOnClickListener {
                val cardname = holder.itemView.et_card_name.text.toString()
                if (cardname.isNotEmpty()) {
                    if (context is tasklist) {
       context.addCardToList(position,cardname)
                    }
                } else {
                    Toast.makeText(context, "Please Enter card Name", Toast.LENGTH_LONG).show()

                }
            }
            holder.itemView.rv_card_list.layoutManager=LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)
            val adapter=cardListAdapter(context,model.cards)
            holder.itemView.rv_card_list.adapter=adapter

            adapter.setOnClickListener(
                object :cardListAdapter.OnClickListener{
                    override fun onClick(cardposition: Int) {
                        if (context is tasklist){
                            context.cardDetails(position,cardposition)
                        }

                    }

                }
            )
           val dividerdecorateitem=DividerItemDecoration(context,
            DividerItemDecoration.VERTICAL)
            holder.itemView.rv_card_list.addItemDecoration(dividerdecorateitem)
            val itemtouch=ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,0
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                   val draggedposition=dragged.adapterPosition
                    val targetposition=target.adapterPosition
                    if (mdraggedfrom==-1){
                        mdraggedfrom=draggedposition
                    }
                    mdraggedto=targetposition
                    Collections.swap(list[position].cards,draggedposition,targetposition)
adapter.notifyItemMoved(draggedposition,targetposition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    TODO("Not yet implemented")
                }
                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    if (mdraggedfrom!= -1 && mdraggedto != -1 && mdraggedfrom!= mdraggedto) {

                        (context as tasklist).updateCardsInTaskList(
                            position,
                            list[position].cards
                        )
                    }


                    mdraggedfrom = -1
                    mdraggedto = -1
                }
                // END
            }
            )
            itemtouch.attachToRecyclerView(holder.itemView.rv_card_list)


        }


        }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Alert")

        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is tasklist) {
                context.deleteTask(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function to get density pixel from pixel
     */
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * A function to get pixel from density pixel
     */
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END