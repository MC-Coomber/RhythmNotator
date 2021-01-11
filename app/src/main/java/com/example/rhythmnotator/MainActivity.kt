package com.example.rhythmnotator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    private val logTag = "MAIN ACTIVITY"
    private lateinit var metronome: Metronome
    private lateinit var recorder: Recorder
    var recording: ShortArray = ShortArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        metronome = Metronome(this)
        recorder = Recorder(metronome)
        Log.d(logTag, "I'M HERE")
    }

    fun onStartClick(view: View) {
        recorder.start()
        recording = recorder.audioBuffer
    }

    fun startMetronome(view: View) {
        metronome.start()
    }

    fun stopMetronome(view: View) {
        metronome.stop()
    }

    companion object RecordingConfig {
        var bpm = 120
        var sampleRate = 4000
        var barsToRecordFor = 2
        var beatsInABar = 4
    }
}