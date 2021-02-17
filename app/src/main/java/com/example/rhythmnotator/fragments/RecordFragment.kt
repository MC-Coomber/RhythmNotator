package com.example.rhythmnotator.fragments

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Vibrator
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

    private var bpm = 60
    private var barsToRecordFor = 1
    private var beatsInABar = 4

    private var buttonTapped = false

    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = activity!!.applicationContext as ExtendedContext
        recorder = Recorder(context)

        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onStart() {
        super.onStart()

        start.setOnClickListener {
            val inputBpm = bpm_input.text
            val inputNumBars = bar_num_input.text
            bpm = inputBpm.toString().toInt()
            barsToRecordFor = inputNumBars.toString().toInt()
            val context = activity!!.applicationContext as ExtendedContext
            context.bpm = bpm
            context.barsToRecordFor = barsToRecordFor

            recorder.init()
            GlobalScope.launch {
                playNumBarsBlocking(1, context.bpm, context.beatsInABar)
                playNumBars(context.barsToRecordFor, context.bpm, context.beatsInABar)
                val recording = recorder.start()

                val context = activity!!.applicationContext as ExtendedContext
                val audioProcessor = AudioProcessor(recording, context)
                val notes = audioProcessor.getNoteData()
                context.currentNoteData = notes
            }
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
            playNumBarsBlocking(1, bpm, beatsInABar)
            Log.d(logTag, "Recording input")
            playNumBars(barsToRecordFor, bpm, beatsInABar)
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

            val context = activity!!.applicationContext as ExtendedContext
            context.currentNoteData = bucketsFinal
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
                soundPool.play(soundId, 1f, 1f, 1, 0, 1F)
            }
        }
    }
}