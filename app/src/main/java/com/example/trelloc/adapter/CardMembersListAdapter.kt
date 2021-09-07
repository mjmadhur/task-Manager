package com.example.trelloc.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloc.R
import com.projemanag.model.SelectedMembers
import kotlinx.android.synthetic.main.item_card_selected_member.view.*

open class CardMembersListAdapter (
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private val assignedmembers:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card_selected_member,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1 && assignedmembers) {
                holder.itemView.iv_add_member.visibility = View.VISIBLE
                holder.itemView.iv_selected_member_image.visibility = View.GONE
            } else {
                holder.itemView.iv_add_member.visibility = View.GONE
                holder.itemView.iv_selected_member_image.visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_baseline_supervised_user_circle_24)
                    .into(holder.itemView.iv_selected_member_image)
            }

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {
        fun onClick()
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
