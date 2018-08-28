package com.example.octo.printstudio

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.octo.printstudio.MainAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class HttpRequestHandler(var context: Context, var rview: RecyclerView)
{
    fun get(url: String)
    {
        val request = okhttp3.Request.Builder().addHeader("X-Api-Key", "F7A30A84F18E436DB4E96C338B807502").url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object :Callback
        {
            override fun onFailure(call: Call?, e: IOException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?)
            {
                val body = response?.body()?.string()
                println(body)


                val gson = GsonBuilder().create()

                val files = gson.fromJson(body, Files::class.java)

                rview.adapter = MainAdapter(files)


            }
        })

    }
}

class Files(val files : List<File>)

class File(val date: Int, val name: String, val origin: String, val type: String)