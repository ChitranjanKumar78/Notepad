package com.chitranjank.notepad

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(context: Context, list: List<Note>) : BaseAdapter() {
    var noteList = list
    var mContext = context

    override fun getCount(): Int {
        return noteList.size
    }

    override fun getItem(position: Int): Any {
        return noteList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.note_itms, parent, false)
        val tvTime = view.findViewById<TextView>(R.id.tv_time)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvDes = view.findViewById<TextView>(R.id.tv_des)

        val note = noteList.get(position)

        tvTime.setText("Last updated on: "+convertLongToTime(note.getTime().toLong()))
        tvTitle.setText(note.getTitle())
        tvDes.setText(note.getDescription())

        return view
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("hh:mm a dd.MM.yyyy ")
        return format.format(date)
    }
}