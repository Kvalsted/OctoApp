package com.example.octo.printstudio

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.octo.printstudio.HttpRequestHandler
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_timelapses.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, timelapses.OnFragmentInteractionListener ,timelapse_info.OnFragmentInteractionListener, CommandLineInterface.OnFragmentInteractionListener, WebSocketListener{
    lateinit var timelapse_fragment : timelapses
    lateinit var tinfo: timelapse_info
    lateinit var cmd: CommandLineInterface
    var top_frag : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        timelapse_fragment = timelapses.newInstance("123", "232")


        nav_view.setNavigationItemSelectedListener(this)
        rview.setBackgroundColor(Color.LTGRAY)
        rview.layoutManager = LinearLayoutManager(this)

        var url : String = "http://80.210.72.202:63500/api/files"



        val request = okhttp3.Request.Builder().addHeader("X-Api-Key", "F7A30A84F18E436DB4E96C338B807502").url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call?, e: IOException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call?, response: Response?)
            {
                val body = response?.body()?.string()
                //println(body)


                val gson = GsonBuilder().create()

                val files = gson.fromJson(body, Files::class.java)

                runOnUiThread {

                rview.adapter = MainAdapter(files)}

            }


        })


        //Instantiate fragments



        tinfo = timelapse_info()
        cmd = CommandLineInterface()
        top_frag = R.id.container

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_stream -> {

                val intent = Intent(this, Stream::class.java)
                startActivity(intent)
                // Handle the camera action
            }
            R.id.nav_timelapses -> {


                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, timelapse_fragment)
                        .addToBackStack("timelapse")
                        .setTransitionStyle(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

            }
            R.id.nav_cmd -> {

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, cmd)
                        .addToBackStack("cmd")
                        .setTransitionStyle(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
    }


    fun gettinfo(): timelapse_info {
        return tinfo
    }


}
