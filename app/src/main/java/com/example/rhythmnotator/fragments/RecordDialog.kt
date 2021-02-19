package com.example.rhythmnotator.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.rhythmnotator.AudioProcessor
import com.example.rhythmnotator.ExtendedContext
import com.example.rhythmnotator.R
import com.example.rhythmnotator.Recorder
import kotlinx.android.synthetic.main.dialog_record.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

class RecordDialog : DialogFragment() {
    private val logTag = "RECORD FRAGMENT"
    private var buttonTapped = false
    private lateinit var recorder: Recorder

    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity!!.applicationContext as ExtendedContext
        recorder = Recorder(context)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_record, null))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onResume() {
        super.onResume()
        countDisplay.text
    }

    private fun record() {
        val extendedContext = activity!!.applicationContext as ExtendedContext

        recorder.init()
        GlobalScope.launch {
            playNumBarsBlocking(1, extendedContext.bpm, extendedContext.beatsInABar)
            playNumBars(extendedContext.barsToRecordFor, extendedContext.bpm, extendedContext.beatsInABar)

            val recording = recorder.start()
            val audioProcessor = AudioProcessor(recording, extendedContext)
            val notes = audioProcessor.getNoteData()
            extendedContext.currentNoteData = notes
        }
    }


    private fun onTapListenClick(view: View) {
        val extendedContext = activity!!.applicationContext as ExtendedContext

        val recordTime = (extendedContext.barsToRecordFor * extendedContext.beatsInABar) / (extendedContext.bpm / 60)
        val excessRecordTime = (1 / (extendedContext.bpm / 60F)) * 2
        val timeMillis = (recordTime + excessRecordTime) * 1000
        val sixteenthNoteTimeMillis = ((60F / extendedContext.bpm) / 4) * 1000
        Log.d(logTag, "16th: $sixteenthNoteTimeMillis total: $timeMillis")
        var i = 0F
        val buckets = ArrayList<Boolean>()
        GlobalScope.launch {
            delay(500)
            playNumBarsBlocking(1, extendedContext.bpm, extendedContext.beatsInABar)
            Log.d(logTag, "Recording input")
            playNumBars(extendedContext.barsToRecordFor, extendedContext.bpm, extendedContext.beatsInABar)
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
            val totalBeats = (extendedContext.beatsInABar * extendedContext.barsToRecordFor) * 4 //number of 16th notes recorded
            val bucketsFinal = buckets.drop(4).subList(0, totalBeats)
            Log.d(logTag, "BUTTON INPUT FINSIHED, BUCKETS: $bucketsFinal")

            extendedContext.currentNoteData = bucketsFinal
        }
    }

    fun onInputButtonClick(view: View) {
        buttonTapped = true
    }

    //Metronome functions
    private suspend fun playNumBarsBlocking(bars: Int, bpm: Int, beatsInABar: Int) {
        val soundId = soundPool.load(context, R.raw.click, 1)
        val interval = 60000 / bpm
        val beats = bars * beatsInABar
        val v = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        for(i in 1..beats) {
            delay(interval.toLong())
            countDisplay.text = i.toString()
            soundPool.play(soundId, 1f, 1f, 1, 0, 1F)
//            v.vibrate(VibrationEffect.createOneShot(50, 100))
        }
    }

    private fun playNumBars(bars: Int, bpm: Int, beatsInABar: Int ) {
        val soundId = soundPool.load(context, R.raw.click, 1)
        val interval = 60000 / bpm
        val beats = bars * beatsInABar
        val v = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        GlobalScope.launch {
            for(i in 1..beats) {
                delay(interval.toLong())
//                v.vibrate(VibrationEffect.createOneShot(50, 100))
                activity!!.runOnUiThread {
                    countDisplay.text = i.toString()
                }
                soundPool.play(soundId, 1f, 1f, 1, 0, 1F)
            }
        }
    }


}