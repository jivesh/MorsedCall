package com.jiv.morsedcall

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.telecom.TelecomManager
import android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER
import android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME
import android.util.Log
import android.view.View
import androidx.core.content.systemService
import androidx.core.net.toUri
import com.jiv.morsedcall.messageTimer.numOutCalls
import kotlinx.android.synthetic.main.activity_dialer.*

object messageTimer {
    public var numInCalls = 0
    public var numOutCalls = 0
    public var outgg = false;
    public var incmg = false;
    public var duration: Long = 0;
    public var msg: Long = 0;
    public var inStart: Long = 0
    public var onb1 = false
    public var onb2 = false
    public var onb3 = false
}

class DialerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialer)
        phoneNumberInput.setText(intent?.data?.schemeSpecificPart)
    }

    override fun onStart() {
        super.onStart()
        offerReplacingDefaultDialer()
        if(messageTimer.onb1 || messageTimer.onb2 || messageTimer.onb3) {
            if (messageTimer.numOutCalls < 2) {
                messageTimer.duration = 12000
                makeCall()
            } else if(messageTimer.numOutCalls == 2) {
                messageTimer.duration = messageTimer.msg
                messageTimer.onb1 = false
                messageTimer.onb2 = false
                messageTimer.onb3 = false
                makeCall()
            }
        }

        button.setOnClickListener() {
            messageTimer.msg = 20000
            messageTimer.outgg = true
            if (messageTimer.numOutCalls < 2) {
                messageTimer.duration = 12000
                messageTimer.onb1 = true
                makeCall()
            } else  {
                messageTimer.duration = messageTimer.msg
                messageTimer.onb1 = false
                makeCall()
            }
        }
        button2.setOnClickListener() {
            messageTimer.msg = 30000;
            messageTimer.outgg = true
            if (messageTimer.numOutCalls < 2) {
                messageTimer.duration = 12000
                messageTimer.onb2 = true
                makeCall()
            } else  {
                messageTimer.duration = messageTimer.msg
                messageTimer.onb2 = false
                makeCall()
            }
        }
        button3.setOnClickListener() {
            messageTimer.msg = 40000;
            messageTimer.outgg = true
            if (messageTimer.numOutCalls < 2) {
                messageTimer.duration = 12000
                messageTimer.onb3 = true
                makeCall()
            } else {
                messageTimer.duration = messageTimer.msg
                messageTimer.onb3 = false
                makeCall()
            }
        }

//        phoneNumberInput.setOnEditorActionListener { _, _, _ ->
//            if(messageTimer.outgg == true) {
//                Log.d("baba", "JERAOOOO")
//                if (messageTimer.numOutCalls < 3) {
//                    messageTimer.duration = 12
//                    makeCall()
//                    true
//                } else if(messageTimer.numOutCalls == 3) {
//                    messageTimer.duration = messageTimer.msg
//                    makeCall()
//                    true
//                }
//            } else {
//                makeCall()
//                true
//            }
//        }
    }

    private fun makeCall() {
                    if (checkSelfPermission(this, CALL_PHONE) == PERMISSION_GRANTED) {
                        val uri = "tel:${phoneNumberInput.text}".toUri()
                        startActivity(Intent(Intent.ACTION_CALL, uri))
                    } else {
                        requestPermissions(this, arrayOf(CALL_PHONE),
                            REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION && PERMISSION_GRANTED in grantResults) {
            makeCall()
        }
    }

    private fun offerReplacingDefaultDialer() {
        if (systemService<TelecomManager>().defaultDialerPackage != packageName) {
            Intent(ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                    .let(::startActivity)
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 0
    }
}
