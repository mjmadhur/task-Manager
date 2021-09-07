package com.example.trelloc.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloc.R
import com.example.trelloc.firebase.Firestore
import com.example.trelloc.models.Board
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_board.view.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.item_board.view.iv_board_image

open class boarditemAdapter(private val context: Context,private var list:ArrayList<Board>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onclickListener:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_board,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      val model=list[position]
        if (holder is MyviewHolder){
            Glide.with(context).load(model.image).centerCrop().placeholder(R.drawable.aa).into(holder.itemView.iv_board_image)
            holder.itemView.tv_name.text=model.name
            holder.itemView.tv_created_by.text="Created By:${model.createdBy}"
            holder.itemView.setOnClickListener{
                if (onclickListener!=null){
                    onclickListener!!.onClick(position, model)
                }
            }
        }

    }

    override fun getItemCount(): Int {
       return list.size
    }
    interface OnClickListener{
        fun onClick(position: Int,model:Board)

    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onclickListener = onClickListener
    }
    fun deleteAt(position: Int){
        list.removeAt(position)
Firestore().deleteBoard()
        notifyDataSetChanged()
    }

    private class MyviewHolder(view: View):RecyclerView.ViewHolder(view)


}


