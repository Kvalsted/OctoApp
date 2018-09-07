package com.example.octo.printstudio

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.file_row.view.*
import kotlinx.android.synthetic.main.timelapse_row.view.*


class TimelapseAdapter(var files: tfiles): RecyclerView.Adapter<TimelapseViewHolder>()
{

    override fun onBindViewHolder(holder: TimelapseViewHolder?, position: Int) {
        val file = files.files.get(position)
        //holder?.view?.tvtl!!.text = file.name
        holder?.file = file

        if (position % 2 != 0)
        {
            if (holder != null) {
                holder.view.setBackgroundColor(Color.LTGRAY)
            }
        }
        else
        {
            if (holder != null) {
                holder.view.setBackgroundColor(Color.WHITE)
            }
        }

        if (holder != null) {
            holder.view.timelapse_textview.text = file.name
            holder.view.filesize.text = "Size: ${file.size}"
            holder.view.added.text = "Added: ${file.date}"
        }
    }
    override fun getItemCount(): Int {

        return files.files.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TimelapseViewHolder {


        val layoutInflater = LayoutInflater.from(parent?.context)
        var cellForRow = layoutInflater.inflate(R.layout.timelapse_row, parent, false)
        return TimelapseViewHolder(cellForRow)


    }




}

class TimelapseViewHolder(val view: View, var file: tfile? = null) : RecyclerView.ViewHolder(view)

{
    lateinit var fileprop: tfiles

    init {
        view.tlbt.setOnClickListener{


            val intent:Intent = Intent(view.context, Timelapsplayer::class.java)
            intent.putExtra("url", file?.url)
            view.context.startActivity(intent)
        }
    }




}