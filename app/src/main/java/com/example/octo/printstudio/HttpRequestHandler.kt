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

    }
}

class Files(val files : List<File>)

class File(val date: Int, val name: String, val origin: String, val type: String, val refs: Refs)

class Refs(val download: String, val resource: String)