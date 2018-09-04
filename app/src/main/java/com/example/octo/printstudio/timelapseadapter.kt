package com.example.octo.printstudio

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.file_row.view.*
import android.support.v7.app.AppCompatActivity



class TimelapseAdapter(var files: tfiles): RecyclerView.Adapter<TimelapseViewHolder>()
{

    override fun onBindViewHolder(holder: TimelapseViewHolder?, position: Int) {
        val file = files.files.get(position)
        holder?.view?.textView4!!.text = file.name
        holder?.file = file

        if (position % 2 != 0)
        {
            holder.view.setBackgroundColor(Color.LTGRAY)
        }
        else
        {
            holder.view.setBackgroundColor(Color.GRAY)
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
        view.setOnClickListener{


            val intent:Intent = Intent(view.context, Timelapsplayer::class.java)
            intent.putExtra("url", file?.url)
            view.context.startActivity(intent)
        }
    }




}