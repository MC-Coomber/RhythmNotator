package com.example.rhythmnotator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    val logTag = "MAIN ACTIVITY"
    private lateinit var metronome: Metronome
    private val recorder: Recorder = Recorder()
    var recording: ShortArray = ShortArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        metronome = Metronome(this)
    }

    fun onStartClick(view: View) {
        recorder.start()
        recording = recorder.audioBuffer
    }

    fun startMetronome(view: View) {
        metronome.play()
    }

    fun stopMetronome(view: View) {
        metronome.stop()
    }
}