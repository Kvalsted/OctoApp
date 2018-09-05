package com.example.octo.printstudio

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_command_line_interface.*
import kotlinx.android.synthetic.main.fragment_command_line_interface.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import okio.ByteString
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.IOException
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.CommonPool
import java.security.Key
import okhttp3.FormBody




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CommandLineInterface.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CommandLineInterface.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CommandLineInterface : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var client: OkHttpClient? = null
    private var thread_running = false
    var run: Boolean = true
    override fun onDestroyView() {
        run = false
        super.onDestroyView()


    }

    private inner class EchoWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            /*webSocket.send("Hello, it's SSaurel !")
            webSocket.send("What's up ?")
            webSocket.send(ByteString.decodeHex("deadbeef"))

            webSocket.close(1000, "Goodbye !")
        */
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            //println("Receiving : " + text!!)


                if (text!!.startsWith("{\"current\":") and run) {

                    println(text.toString())
                    //println("hej")
                    val gson = GsonBuilder().create()
                    val files = gson.fromJson(text.toString(), Current::class.java)

                    async(CommonPool)
                    {
                        bg{
                            activity.runOnUiThread {
                                for (t in files.current.logs) {
                                    val tv: TextView = TextView(activity)
                                    tv.text = t
                                    activity.scrolly.ll.addView(tv)
                                    activity.scrolly.post(Runnable { activity.scrolly.fullScroll(View.FOCUS_DOWN) })
                                }
                            }

                        }
                    }
                    /*
                */
                }




        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            //println("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            //webSocket.close(1000, null)
            println("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            super.onFailure(webSocket, t, response)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        run = true
        val v = inflater.inflate(R.layout.fragment_command_line_interface, container, false)

        //Send GCode command
        v.send.setOnClickListener {
            var client = OkHttpClient()
            var inc_cmd = v.textin.text
            val url: String = "http://80.210.72.202:63500/api/printer/command"
            async(UI) {

                bg {

                    val json = """
                {
                    "command":"${inc_cmd.toString().toUpperCase()}"
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


        if(thread_running == false) {
            client = OkHttpClient()
            val request2 = Request.Builder().url("http://80.210.72.202:63500/sockjs/websocket").build()
            val listener = EchoWebSocketListener()

            client!!.retryOnConnectionFailure()
            val ws = client!!.newWebSocket(request2, listener)
            client!!.dispatcher().executorService().shutdown();
            thread_running = true
        }

        return v
    }



    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Terminating")
        thread_running = false
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommandLineInterface.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CommandLineInterface().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
class Current(val current: Logs)
class Logs(val logs: List<String>)