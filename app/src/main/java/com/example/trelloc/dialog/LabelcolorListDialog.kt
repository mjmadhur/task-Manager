package com.example.trelloc.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trelloc.R
import com.example.trelloc.adapter.LabelcolorsListAdapter
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LabelcolorListDialog (
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {

    private var adapter: LabelcolorsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }
    private fun setUpRecyclerView(view: View) {
        view.tvTitle.text = title
        view.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelcolorsListAdapter(context, list, mSelectedColor)
        view.rvList.adapter = adapter

        adapter!!.onItemClickListener = object : LabelcolorsListAdapter.OnItemClickListener {

            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }
    protected abstract fun onItemSelected(color: String)

}