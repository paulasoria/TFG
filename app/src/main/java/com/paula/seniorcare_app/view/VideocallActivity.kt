package com.paula.seniorcare_app.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

class VideocallActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callId = intent.getStringExtra("callId").toString()
        val sp: SharedPreferences = getSharedPreferences("JWT_FILE", MODE_PRIVATE)
        val jwt = sp.getString("JWT", null)

        val serverURL: URL = try {
            URL("https://jitsi.paulasoria.tk")
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

        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setToken(jwt)
            .setRoom(callId)
            .setVideoMuted(false)
            .setAudioMuted(false)
            .build()
        JitsiMeetActivity.launch(this, options)
    }
}