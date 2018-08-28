package com.example.octo.printstudio

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.file_row.view.*

class MainAdapter(val files: Files): RecyclerView.Adapter<CustomViewHolder>()
{
    override fun getItemCount(): Int {
        return files.files.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {


        val layoutInflater = LayoutInflater.from(parent?.context)
        var cellForRow = layoutInflater.inflate(R.layout.file_row, parent, false)
        return CustomViewHolder(cellForRow)


    }

    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
        val file = files.files.get(position)
        holder?.view?.textView4!!.text = file.name
        if (position % 2 != 0)
        {
            holder.view.setBackgroundColor(Color.LTGRAY)
        }
        else
        {
            holder.view.setBackgroundColor(Color.GRAY)
        }
    }


}

class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)
{

    /*init {
        view.setOnClickListener{
            val intent = Intent(view.context, filedetail::class.java)
            intent.putExtra("name", 25)
            view.context.startActivity(intent)
        }
    }*/




}