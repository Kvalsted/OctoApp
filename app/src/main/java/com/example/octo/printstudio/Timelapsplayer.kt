package com.example.octo.printstudio

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


        setContentView(R.layout.activity_timelapsplayer)
        mSurface = surfaceView
        var etUrl= "http://80.210.72.202:63500/downloads/timelapse/rods_20180714222819.mpg"
        videoController= VideoController(this)
        videoController!!.mSurface=mSurface

        videoController!!.createPlayer(etUrl)
    }
}
