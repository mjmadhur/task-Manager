package com.example.trelloc.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trelloc.R
import com.example.trelloc.models.Card
import com.example.trelloc.tasklist
import com.projemanag.model.SelectedMembers
import kotlinx.android.synthetic.main.item_card.view.*

open class cardListAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            holder.itemView.tv_card_name.text = model.name
            if ((context as tasklist).mAssignedMemDetails.size>0){
                val selectedMemberslist:ArrayList<SelectedMembers> =ArrayList()
                for (i in context.mAssignedMemDetails.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMemDetails[i].id==j ){
                            val selectedmembers=SelectedMembers(
                                context.mAssignedMemDetails[i].id,
                                context.mAssignedMemDetails[i].image
                            )
                            selectedMemberslist.add(selectedmembers)
                        }
                    }
                }
                if (selectedMemberslist.size>0){
if (selectedMemberslist.size==1&& selectedMemberslist[0].id==model.createdBy){
    holder.itemView.rv_card_selected_members_list.visibility=View.GONE
}else{
    holder.itemView.rv_card_selected_members_list.visibility=View.VISIBLE
    holder.itemView.rv_card_selected_members_list.layoutManager=GridLayoutManager(context,4)
    val adapter=CardMembersListAdapter(context,selectedMemberslist,false)
    holder.itemView.rv_card_selected_members_list.adapter=adapter
    object :CardMembersListAdapter.OnClickListener{
        override fun onClick() {
            if (onClickListener!=null){
                onClickListener!!.onClick(position)
            }
        }

    }

}
                }else{
                    holder.itemView.rv_card_selected_members_list.visibility=View.GONE
                }
            }
        holder.itemView.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onClick(position)
            }
        }
            if (model.labelColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility=View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.view_label_color.visibility=View.GONE
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick(position: Int)
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
