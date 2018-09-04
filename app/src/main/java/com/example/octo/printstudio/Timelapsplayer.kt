package com.example.octo.printstudio

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_timelapsplayer.*

class Timelapsplayer : AppCompatActivity() {

    private var mSurface: SurfaceView? = null

    private var videoController: VideoController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        var url: String = intent.getStringExtra("url")
        println("url")
        println (url)
        setContentView(R.layout.activity_timelapsplayer)
        mSurface = surfaceView
        val etUrl= "http://80.210.72.202:63500$url"
        videoController= VideoController(this)
        videoController!!.mSurface=mSurface

        videoController!!.createPlayer(etUrl)
    }
}
