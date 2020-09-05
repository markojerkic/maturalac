package com.markojerkic.drzavnamatura

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ExamsListAdapter(private val exams: List<String>,
                       private val layoutInflater: LayoutInflater): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, container: ViewGroup?): View {
        var view: View? = null
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.exam_list_item, container, false)
            createItem(view, position)
            return view
        }
        createItem(convertView, position)
        return convertView
    }

    private fun createItem(view: View?, position: Int) {
        val examNameTextView = view!!.findViewById<TextView>(R.id.exam_name_textview)
        examNameTextView.text = exams[position]
    }

    override fun getItem(p0: Int): Any {
        return exams[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return exams.size
    }

}