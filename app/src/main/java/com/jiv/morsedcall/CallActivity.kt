package com.jiv.morsedcall

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import androidx.core.view.isVisible
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_call.*
import java.util.concurrent.TimeUnit
import android.os.Handler
import android.util.Log
import androidx.core.os.postDelayed
import com.jiv.morsedcall.lcalls.first

object lcalls {
    public var first: Long = 0;
    public var second: Long= 0;
    public var third: Long = 0;
    public var msg = ""
}
class CallActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        number = intent.data.schemeSpecificPart
    }

    override fun onStart() {
        super.onStart()

        if(messageTimer.outgg ==true) {
            val tCurrent = System.currentTimeMillis()
            val tRemain: Long = messageTimer.duration
            Handler().postDelayed({
                OngoingCall.hangup()
                if(messageTimer.numOutCalls<2) {
                    messageTimer.numOutCalls++
                    Log.d("primer call", tRemain.toString())
                } else {
                    messageTimer.outgg = false;
                    messageTimer.numOutCalls = 0
                    Log.d("Message sending", tRemain.toString())
                }
            }, tRemain)
        } else {
            messageTimer.inStart = System.currentTimeMillis()
            Log.d("inTimer updated", "boomboom")
        }
        answer.setOnClickListener {
            OngoingCall.answer()
        }

        hangup.setOnClickListener {
            OngoingCall.hangup()
        }

        OngoingCall.state
            .subscribe(::updateUi)
            .addTo(disposables)

        OngoingCall.state
            .filter { it == Call.STATE_DISCONNECTED }
            .delay(1, TimeUnit.SECONDS)
            .firstElement()
            .subscribe { finish() }
            .addTo(disposables)
    }

    @SuppressLint("SetTextI18n")
    private fun updateUi(state: Int) {
        callInfo.text = "${state.asString().toLowerCase().capitalize()}\n$number"

        answer.isVisible = state == Call.STATE_RINGING
        hangup.isVisible = state in listOf(
            Call.STATE_DIALING,
            Call.STATE_RINGING,
            Call.STATE_ACTIVE
        )
    }

    override fun onStop() {
        super.onStop()
        val tEnd = System.currentTimeMillis() - messageTimer.inStart
        Log.d("TIMEFORCALL", tEnd.toString())

        if (10000 < tEnd && tEnd < 15000) {
            lcalls.msg = "I\'ve reached home safely"
            val intent = Intent(this, MsgActivity::class.java)
            startActivity(intent)
        } else if (15000 <tEnd && tEnd < 20000) {
            lcalls.msg = "I\'m here at the meeting point, where are you?"
            val intent = Intent(this, MsgActivity::class.java)
            startActivity(intent)
        } else if (tEnd >20000){
            lcalls.msg = "My internet\'s busted, help me top up"
            val intent = Intent(this, MsgActivity::class.java)
            startActivity(intent)
        }



        val a: Long = 0;
        Log.d("HELLO1", lcalls.first.toString())
        Log.d("HELLO2", lcalls.second.toString())
        Log.d("HELLO3", lcalls.third.toString())
        if(lcalls.first==a)
        {
            lcalls.first = System.currentTimeMillis()
            Log.d("heee", "updated dirst")
        } else if(lcalls.second==a)
        {
            lcalls.second = System.currentTimeMillis()
        } else if(lcalls.third==a)
        {
            lcalls.third = System.currentTimeMillis()
        } else
        {
            val m = lcalls.second - lcalls.first
            Log.d("heehorr", m.toString())
            if((lcalls.second - lcalls.first)<12000 && (lcalls.third - lcalls.second)<12000) {
                if (tEnd < 10000) {
                    lcalls.msg = "I\'ve reached home safely"
                } else if (tEnd < 20000) {
                    lcalls.msg = "I\'m here at the meeting point, where are you?"
                } else {
                    lcalls.msg = "My internet\'s busted, help me top up"
                }
                val intent = Intent(this, MsgActivity::class.java)
                startActivity(intent)
            } else {
                lcalls.first = lcalls.second
                lcalls.second = lcalls.third
                lcalls.third = System.currentTimeMillis()
            }
        }
        disposables.clear()


    }

    companion object {
        fun start(context: Context, call: Call) {
            Intent(context, CallActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.details.handle)
                .let(context::startActivity)
        }
    }
}
