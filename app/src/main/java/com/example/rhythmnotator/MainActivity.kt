package com.example.rhythmnotator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    val logTag = "MAIN ACTIVITY"

    val recorder: Recorder = Recorder()
    var recording: ShortArray = ShortArray(0)
    var silence: ShortArray = ShortArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun onStartClick(view: View) {
        recorder.start()
        recording = recorder.audioBuffer


    }

    fun onRecordSilenceClick(view: View) {
        recorder.start()
        silence = recorder.audioBuffer
    }
}