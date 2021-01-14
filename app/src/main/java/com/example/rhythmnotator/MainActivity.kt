package com.example.rhythmnotator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
    }

    fun onStartClick(view: View) {
        val inputBpm = bpm_input.text
        val inputNumBars = bar_num_input.text
        bpm = inputBpm.toString().toInt()
        barsToRecordFor = inputNumBars.toString().toInt()

        recorder.init()
        GlobalScope.launch {
            recorder.start()
            recording = recorder.audioBuffer
        }
    }

    companion object RecordingConfig {
        var bpm = 120
        var sampleRate = 44100
        var barsToRecordFor = 2
        var beatsInABar = 4
    }
}

