package com.paula.seniorcare_app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

class VideocallActivity() : AppCompatActivity() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)

        val sp: SharedPreferences = getSharedPreferences("JWT_FILE", MODE_PRIVATE)
        val jwt = sp.getString("JWT", null)
        val callId = intent.getStringExtra("callId").toString()

        // Initialize default options for Jitsi Meet conferences.
        val serverURL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            URL("https://jitsi.emeriti.es")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }

        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setToken(jwt)
            .setFeatureFlag("welcomepage.enabled", false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        //registerForBroadcastMessages()
        EventBus.getDefault().register(this)

        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setToken(jwt)
            .setRoom(callId) // Settings for audio and video
            .setAudioMuted(false)
            .setVideoMuted(false)
            .build()

        JitsiMeetActivity.launch(this, options)
    }

    private val initBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getParcelableExtra<NotificationData>("data")
            when (type!!.type) {
                INVITATION_ACCEPTED -> {
                    findNavController().navigateUp()
                    viewModel.startCalling(requireContext(), meetingRoom, isAudio)
                }
                INVITATION_REJECTED -> {
                    findNavController().navigateUp()
                }
                INVITATION_CANCEL -> {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                initBroadcastManager,
                IntentFilter(INVITATION_RESPONSE)
            )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(initBroadcastManager)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)

        val callId = intent.getStringExtra("callId").toString()
        val sp: SharedPreferences = getSharedPreferences("JWT_FILE", MODE_PRIVATE)
        val jwt = sp.getString("JWT", null)

        val serverURL: URL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            URL("https://jitsi.paulasoria.tk")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }

        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setToken(jwt)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setToken(jwt)              //HE PROBADO CON LA URL DIRECTAMENTE EN EL NAVEGADOR Y DICE QUE NO TENGO PERMISOS
            .setRoom(callId)
            .setVideoMuted(false)
            .setAudioMuted(false)
            .build()
        JitsiMeetActivity.launch(this, options)
    }

    //BUTTON COLGAR + HTTP FUNCTION
}