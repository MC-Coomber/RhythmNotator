package com.example.rhythmnotator

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {
    private val logTag = "MAIN ACTIVITY"
    private lateinit var metronome: Metronome
    private lateinit var recorder: Recorder
    private var recording: ShortArray = ShortArray(0)
    private var buttonTapped = false

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
            val recording = recorder.start()

            val audioProcessor = AudioProcessor(recording)
            val notes = audioProcessor.getNoteData()
            val noteRenderer = NoteRenderer(note_holder, this@MainActivity)

            runOnUiThread {
                noteRenderer.renderNoteData(notes)
            }
        }
    }

    fun onPlayClick(view: View) {
        val playback = Playback(this)
        val rhythm = arrayListOf(true, true, true, true, true, false, false, false, true, false, false, false ,true, false, false, false)

        playback.playRhythm(200, rhythm)
    }

    fun onTapListenClick(view: View) {
        val recordTime = (barsToRecordFor * beatsInABar) / (bpm / 60)
        val excessRecordTime = (1 / (bpm / 60F)) * 2
        val timeMillis = (recordTime + excessRecordTime) * 1000
        val sixteenthNoteTimeMillis = ((60F / bpm) / 4) * 1000
        Log.d(logTag, "16th: $sixteenthNoteTimeMillis total: $timeMillis")
        var i = 0F
        val buckets = ArrayList<Boolean>()
        GlobalScope.launch {
            metronome.playNumBarsBlocking(1)
            Log.d(logTag, "Recording input")
            metronome.playNumBars(barsToRecordFor)
            while (i < timeMillis) {
                var input = false
                val timer = Timer()
                timer.scheduleAtFixedRate(0, 10) {
                    if (buttonTapped) {
                        input = true
                        buttonTapped = false
                    }
                }
                delay(sixteenthNoteTimeMillis.toLong())
                if (input) {
                    buckets.add(true)
                } else {
                    buckets.add(false)
                }
                timer.cancel()
                i += sixteenthNoteTimeMillis
            }
            val totalBeats = (beatsInABar * barsToRecordFor) * 4 //number of 16th notes recorded
            val bucketsFinal = buckets.drop(4).subList(0, totalBeats)
            Log.d(logTag, "BUTTON INPUT FINSIHED, BUCKETS: $bucketsFinal")

            runOnUiThread {
                val noteRenderer = NoteRenderer(note_holder, this@MainActivity)
                noteRenderer.renderNoteData(bucketsFinal)
            }
        }
    }

    fun onInputButtonClick(view: View) {
        buttonTapped = true
    }

    companion object RecordingConfig {
        var bpm = 120
        var sampleRate = 44100
        var barsToRecordFor = 1
        var beatsInABar = 4
    }
}

