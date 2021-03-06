package com.example.octo.printstudio

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.octo.printstudio.HttpRequestHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_timelapses.*
import okhttp3.*
import java.io.IOException
import okhttp3.WebSocket
import okio.ByteString
import okhttp3.WebSocketListener
import okhttp3.OkHttpClient
import android.widget.TextView
import android.system.Os.shutdown
import android.view.View
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.lang.IllegalStateException


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, timelapses.OnFragmentInteractionListener ,timelapse_info.OnFragmentInteractionListener, CommandLineInterface.OnFragmentInteractionListener{
    lateinit var timelapse_fragment : timelapses
    lateinit var tinfo: timelapse_info
    lateinit var cmd: CommandLineInterface
    var top_frag : Int = 0

    private val client: OkHttpClient? = null

    private inner class EchoWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocket.send("Hello, it's SSaurel !")
            webSocket.send("What's up ?")
            webSocket.send(ByteString.decodeHex("deadbeef"))
            webSocket.close(1000, "Goodbye !")
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            println("Receiving : " + text!!)
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            println("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            webSocket.close(1000, null)
            println("Closing : $code / $reason")
        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


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
        printprog.visibility = View.GONE
        printprogtext.visibility = View.GONE
        pause_btn.visibility = View.GONE
        stop.visibility = View.GONE


        val request = Request.Builder().addHeader("X-Api-Key", "F7A30A84F18E436DB4E96C338B807502").url(url).build()

        var client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?)
            {
                val body = response?.body()?.string()
                println(body)


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
        client = OkHttpClient()


        stop.setOnClickListener {

            var client = OkHttpClient()
            val url : String = "http://80.210.72.202:63500/api/job"
            val json = """
                {
                    "command":"cancel"
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

        pause_btn.setOnClickListener {

            var client = OkHttpClient()
            val url : String = "http://80.210.72.202:63500/api/job"
            val json = """
                {
                    "command":"pause",
                    "action": "pause"
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
        async(CommonPool)
        {
            bg {
                while (true) {

                    getprinterstate()
                    Thread.sleep(2000)

                }

            }
        }
        bt.text = "hej"
        nt.text = "25"


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
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

            }
            R.id.nav_cmd -> {

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, cmd)
                        .addToBackStack("cmd")
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
            }
            R.id.nav_manage -> {

                // Handle the camera action


            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("SetTextI18n")
    fun getprinterstate()
    {
        var perc = "%%"
        var prog: Double = 35.6
        var inpr: String = "Printing Progress: $prog Percent"
        printprogtext.text = inpr

        var url : String = "http://80.210.72.202:63500/api/printer"



        val request = Request.Builder().addHeader("X-Api-Key", "F7A30A84F18E436DB4E96C338B807502").url(url).build()
        var client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call?, e: IOException?) {
                println("lol")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()

                println(body)
                val gson = GsonBuilder().create()


                try {
                    val files = gson.fromJson(body, Info::class.java)
                    println("hej")
                    println(files.state.text)
                    println(files.temperature.bed.actual)

                    async(CommonPool)
                    {
                        state.text = files.state.text
                        bt.text = files.temperature.bed.actual.toString().slice(IntRange(0,3))
                        nt.text = files.temperature.tool0.actual.toString().slice(IntRange(0,3))

                        if(files.state.text == "Printing")
                        {

                            runOnUiThread {
                                printprog.visibility = View.VISIBLE
                                printprogtext.visibility = View.VISIBLE
                                pause_btn.visibility = View.VISIBLE
                                stop.visibility = View.VISIBLE

                            }
                            println("Getting Job State")
                            getprintjobstate(files)
                        }

                        else if(files.state.text == "Paused")
                        {
                            val draw = ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow_black_24dp, null)
                                pause_btn.setImageDrawable(draw)
                        }
                    }
                }

                catch(e: IllegalStateException) {

                }
            }


        })
    }

    fun getprintjobstate(info: Info)
    {

        var url : String = "http://80.210.72.202:63500/api/job"

        val request = Request.Builder().addHeader("X-Api-Key", "F7A30A84F18E436DB4E96C338B807502").url(url).build()
        var client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call?, e: IOException?) {
                println("lol")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()

                println(response)
                val gson = GsonBuilder().create()

                try {
                    val files = gson.fromJson(body, Progress::class.java)
                    println("timeleft ${files.progress.printTimeLeft}")
                    println("estimated print time: ${files.estimatedPrintTime}")
                    println("printtime: ${files.progress.printTime}")
                    println("completion: ${files.progress.completion}")


                    printprog.progress = files.progress.completion.toInt()
                    printprogtext.text = "Printing Progress: ${files.progress.completion.toString()}"
                    bt.text = "${info.temperature.bed.actual.toString().slice(IntRange(0,3))} / ${info.temperature.bed.target}"
                    nt.text = "${info.temperature.tool0.actual.toString().slice(IntRange(0,3))} / ${info.temperature.tool0.target}"



                    }



                catch(e: IllegalStateException) {

                }
            }


        })

    }

    override fun onFragmentInteraction(uri: Uri) {
    }




    fun gettinfo(): timelapse_info {
        return tinfo
    }


    class Info(val state: text, val temperature: Temperature )

    class text(val text: String)
    class Temperature(val bed: Bed, val tool0: Tool0)
    class Bed(val actual: Double, val offset:Int, val target: Int)
    class Tool0(val actual: Double, val offset:Int, val target: Int)


    class Progress(val progress: Pgress, val estimatedPrintTime: Int)
    class Pgress(val completion: Double, val printTime: Int, val printTimeLeft: Int)



}
