package com.example.octo.printstudio

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.experimental.CommonPool
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.jetbrains.anko.backgroundColor


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
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
                if (text!!.startsWith("{\"current\":") and run) {

                    println(text.toString())
                    val gson = GsonBuilder().create()
                    val files = gson.fromJson(text.toString(), Current::class.java)

                    async(CommonPool)
                    {
                        bg{
                            activity.runOnUiThread {
                                for (t in files.current.logs) {
                                    val tv: TextView = TextView(activity)
                                    tv.setTextColor(Color.YELLOW)
                                    tv.textSize = 24.00f
                                    tv.text = t
                                    println("backy")
                                    activity.scrolly.ll.addView(tv)
                                    activity.scrolly.post(Runnable { activity.scrolly.fullScroll(View.FOCUS_DOWN) })
                                }
                            }

                        }
                    }
                }




        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
            //println("Receiving bytes : " + bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
            println("Closing : $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            //super.onFailure(webSocket, t, response)
            println("failed to open socket")
            println(t)
            webSocket!!.close(1000, "something happened")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    fun sendGCode() {
        var client = OkHttpClient()
        var inc_cmd = activity.textin.getText().toString()
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        run = true
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = inflater.inflate(R.layout.fragment_command_line_interface, container, false)

        //Send GCode command
        v.textin.setOnEditorActionListener(OnEditorActionListener { textView, keyCode, keyEvent ->
            if (keyCode == EditorInfo.IME_ACTION_SEND) {
                imm.hideSoftInputFromWindow(getView()!!.getWindowToken(), 0)
                sendGCode()
                v.textin.text.clear()
                true
            } else  false
        })

        if(thread_running == false) {
            println("Creating listener")


                    client = OkHttpClient()
                    val request2 = Request.Builder().url("ws://80.210.72.202:63500/sockjs/websocket").build()
                    val listener = EchoWebSocketListener()

                    //client!!.retryOnConnectionFailure()
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