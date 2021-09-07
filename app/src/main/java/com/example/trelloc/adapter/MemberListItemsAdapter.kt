package com.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloc.R
import com.example.trelloc.models.Constants
import com.example.trelloc.models.User
import kotlinx.android.synthetic.main.item_member.view.*


open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>,
    private var onClickListener: OnClickListener? = null

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_supervised_user_circle_24)
                .into(holder.itemView.iv_member_image)

            holder.itemView.tv_member_name.text = model.name
            holder.itemView.tv_member_email.text = model.email
            if (model.selected){
                holder.itemView.iv_selected_member.visibility=View.VISIBLE
            }
            else{
                holder.itemView.iv_selected_member.visibility=View.GONE
            }
            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    if (model.selected){
                        onClickListener!!.onclick(position,model,Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onclick(position,model,Constants.SELECT)
                    }
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
interface OnClickListener {
    fun onclick(position: Int, user: User, action: String)
}
    fun setOnClickListener(onclickListener:OnClickListener){
this.onClickListener=onclickListener
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END