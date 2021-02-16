package com.example.rhythmnotator.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

class RecordFragment : Fragment() {
    private val logTag = "RECORD FRAGMENT"

    private lateinit var recorder: Recorder
    private lateinit var metronome: Metronome

    private var bpm = 60
    private var barsToRecordFor = 1
    private var beatsInABar = 4

    private var buttonTapped = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recorder = Recorder(metronome)
        metronome = Metronome(activity!!.applicationContext)
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    fun onStartClick(view: View) {
        val inputBpm = bpm_input.text
        val inputNumBars = bar_num_input.text
        bpm = inputBpm.toString().toInt()
        barsToRecordFor = inputNumBars.toString().toInt()
        val context = activity!!.applicationContext as ExtendedContext
        context.bpm = bpm
        context.barsToRecordFor = barsToRecordFor

        recorder.init()
        GlobalScope.launch {
            val recording = recorder.start()

            val audioProcessor = AudioProcessor(recording, activity!!.applicationContext as ExtendedContext)
            val notes = audioProcessor.getNoteData()
//            val noteRenderer = NoteRenderer(note_holder, this@MainActivity)
//
//            activity!!.runOnUiThread {
//                noteRenderer.renderNoteData(notes)
//            }
        }
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
            metronome.playNumBarsBlocking(1, bpm, beatsInABar)
            Log.d(logTag, "Recording input")
            metronome.playNumBars(barsToRecordFor, bpm, beatsInABar)
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

//            activity!!.runOnUiThread {
//                val noteRenderer = NoteRenderer(note_holder, this@MainActivity)
//                noteRenderer.renderNoteData(bucketsFinal)
//            }
        }
    }

    fun onInputButtonClick(view: View) {
        buttonTapped = true
    }
}