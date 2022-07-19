package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import org.jitsi.meet.sdk.*
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL


class VideocallActivity : AppCompatActivity() {

    //private var mFunctions: FirebaseFunctions? = null

    /*private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }*/

    //¿ON MESSAGE EVENT?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)

        //mFunctions = FirebaseFunctions.getInstance()
        //mFunctions!!.useEmulator(Constants.URL, Constants.PORT)

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
            .setToken("MyJWT")
            // When using JaaS, set the obtained JWT her
            // Different features flags can be set
            // .setFeatureFlag("toolbox.enabled", false)
            // .setFeatureFlag("filmstrip.enabled", false)
            .setFeatureFlag("welcomepage.enabled", false)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        //registerForBroadcastMessages()

        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setToken("MyJWT")
            .setRoom("CallID") // Settings for audio and video
            .setAudioMuted(false)
            .setVideoMuted(false)
            .build()
        // Launch the new activity with the given options. The launch() method takes care
        // of creating the required Intent and passing the options.
        JitsiMeetActivity.launch(this, options)
    }

    //¿ON DESTROY?

    /*private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()
        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }*/

    // Example for handling different JitsiMeetSDK events
    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.type) {
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference Joined with url%s", event.data["url"])
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s", event.data["name"])
                BroadcastEvent.Type.CONFERENCE_TERMINATED -> endCall()
                else -> Timber.i("Received event: %s", event.type)
            }
        }
    }

    private fun endCall() {

    }

    // Example for sending actions to JitsiMeetSDK
    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this.applicationContext).sendBroadcast(hangupBroadcastIntent)
    }
}