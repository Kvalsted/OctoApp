package com.example.octo.printstudio

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.file_row.view.*
import okhttp3.*
import java.io.IOException

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
        holder?.view?.tvtl!!.text = file.name
        holder?.res = file.refs
        if (position % 2 != 0)
        {
            holder.view.setBackgroundColor(Color.LTGRAY)
        }
        else
        {
            holder.view.setBackgroundColor(Color.WHITE)
        }
    }


}

class CustomViewHolder(val view: View, var res: Refs? = null) : RecyclerView.ViewHolder(view)
{

    init {
        view.print.setOnClickListener{
            println("got a request for resource ${res!!.resource}")






            var client = OkHttpClient()
            val url : String = res!!.resource
            val json = """
                {
                    "command":"select",
                    "print": "true"
                }
                """.trimIndent()

            val body = RequestBody.create(MediaType.parse("application/json"), json)
            val request = Request.Builder()
                    .url(url)
                    .addHeader("X-Api-Key", "F7A30A84F18E436DB4E96C338B807502")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call?, response: Response?) {

                    println("Response: ${response}")
                    println("JSON: ${json}")
                }
            })












        }
    }




}