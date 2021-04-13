package com.example.rhythmnotator.fragments

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.utils.AudioProcessor
import com.example.rhythmnotator.utils.ExtendedContext
import com.example.rhythmnotator.R
import com.example.rhythmnotator.activities.MainActivity
import com.example.rhythmnotator.utils.Recorder
import com.example.rhythmnotator.databinding.FragmentRecordBinding
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

class RecordFragment : Fragment() {
    private val logTag = "RECORD FRAGMENT"

    private lateinit var binding: FragmentRecordBinding

    private var buttonTapped = false
    private var isRecording = false

    private lateinit var recorder: Recorder

    private val scope = MainScope()
    private var recordJob: Job = Job()
    private var metronomeJob: Job = Job()

    private lateinit var v: Vibrator

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
    ): View {
        binding = FragmentRecordBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val context = activity!!.applicationContext as ExtendedContext

        recorder = Recorder(context)

        v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        //Initialize Start Button
        binding.record.setOnClickListener {
            isRecording = true
            if (binding.tapInputSwitch.isChecked) {
                recordJob = scope.launch {
                    recordTaps()
                }
            } else {
                recordJob = scope.launch {
                    record()
                }
            }
            recordJob.start()
            flipButtonVisibility(true)
        }

        //Initialize Cancel Button
        binding.cancel.setOnClickListener {
            cancel()
        }

        //Initialize Bars to Record For button
        binding.numBar.setOnClickListener {
            if (!isRecording) {
                val bottomDialog = NumBarDialogFragment(binding.numBarVal)
                bottomDialog.show(activity!!.supportFragmentManager, "Dialog")
            } else {
                Toast.makeText(context, getString(R.string.cannot_edit), Toast.LENGTH_SHORT).show()
            }
        }
        binding.numBarVal.text = context.barsToRecordFor.toString()

        //Initialize Beats Per Bar button
        binding.beatsPerBar.setOnClickListener {
            if(!isRecording) {
                val bottomDialog = BeatsPerBarDialogFragment(binding.beatsPerBarVal)
                bottomDialog.show(activity!!.supportFragmentManager, "Dialog")
            } else {
                Toast.makeText(context, getString(R.string.cannot_edit), Toast.LENGTH_SHORT).show()
            }
        }
        binding.beatsPerBarVal.text = context.beatsInABar.toString()

        //Initialize slider
        binding.tempoSlider.addOnChangeListener { _, value, _ ->
            binding.bpmVal.text = value.toInt().toString()
            context.bpm = value.toInt()
        }
        binding.tempoSlider.labelBehavior = LabelFormatter.LABEL_GONE
        binding.tempoSlider.valueFrom = 20F
        binding.tempoSlider.valueTo = 220F
        binding.tempoSlider.value = context.bpm.toFloat()
        binding.tempoSlider.stepSize = 1F

        //Initialize tap area
        binding.tapInput.setOnClickListener {
            buttonTapped = true
            v.vibrate(VibrationEffect.createOneShot(50, 100))
        }

        binding.tapInputSwitch.setOnCheckedChangeListener { _, isChecked ->
            context.useTap = isChecked
        }
        binding.tapInputSwitch.isChecked = context.useTap

        binding.metronomeVibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            context.useMetronomeVibrate = isChecked
        }
        binding.metronomeVibrateSwitch.isChecked = context.useMetronomeVibrate

        binding.barNumDisplay.visibility = INVISIBLE
    }

    override fun onPause() {
        super.onPause()
        cancel()
    }

    private suspend fun record() = withContext(Dispatchers.Default) {
        val extendedContext = activity!!.applicationContext as ExtendedContext
        recorder.init()
        playNumBarsBlocking(1, extendedContext.bpm, extendedContext.beatsInABar, extendedContext.useMetronomeVibrate)
        playNumBars(extendedContext.barsToRecordFor, extendedContext.bpm, extendedContext.beatsInABar, extendedContext.useMetronomeVibrate)

        val recording = recorder.start()
        //If the recording has not been cancelled...
        if (recording.isNotEmpty()) {
            val audioProcessor = AudioProcessor(recording, extendedContext)
            val notes = audioProcessor.getNoteData()
            extendedContext.currentNoteData = notes
            extendedContext.recordedBpm = extendedContext.bpm
            isRecording = false
            activity!!.runOnUiThread {
                flipButtonVisibility(false)
                showSuccessSnackbar()
            }
        }
    }

    private fun cancel() {
        isRecording = false
        recorder.stop()
        recordJob.cancel()
        metronomeJob.cancel()
        binding.barNumDisplay.visibility = INVISIBLE
        binding.countDisplay.text = "1"

        binding.tapInputContainer.visibility = GONE
        flipButtonVisibility(false)
    }

    private fun flipButtonVisibility(isRecording: Boolean) {
        if (isRecording) {
            binding.record.visibility = GONE
            binding.cancel.visibility = VISIBLE
        } else {
            binding.record.visibility = VISIBLE
            binding.cancel.visibility = GONE
        }
    }

    private fun showSuccessSnackbar() {
        val snackbar = Snackbar.make(binding.recordParent, "Recording finished", Snackbar.LENGTH_LONG)
            .setAction(R.string.view) {
                val activity = activity as MainActivity
                activity.switchFragment(R.id.playback_item)
            }
        snackbar.show()
    }

    private suspend fun recordTaps() = withContext(Dispatchers.Default) {
        activity!!.runOnUiThread {
            binding.tapInputContainer.visibility = VISIBLE
        }

        val extendedContext = activity!!.applicationContext as ExtendedContext
        val recordTime = (extendedContext.barsToRecordFor * extendedContext.beatsInABar) / (extendedContext.bpm / 60)
        val excessRecordTime = (1 / (extendedContext.bpm / 60F)) * 2
        val timeMillis = (recordTime + excessRecordTime) * 1000
        val sixteenthNoteTimeMillis = ((60F / extendedContext.bpm) / 4) * 1000
        var i = 0F
        val buckets = ArrayList<Boolean>()

        delay(500)
        playNumBarsBlocking(1, extendedContext.bpm, extendedContext.beatsInABar, extendedContext.useMetronomeVibrate)
        Log.d(logTag, "Recording input")
        playNumBars(extendedContext.barsToRecordFor, extendedContext.bpm, extendedContext.beatsInABar, extendedContext.useMetronomeVibrate)
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

        activity!!.runOnUiThread {
            binding.tapInputContainer.visibility = GONE
            flipButtonVisibility(false)
            showSuccessSnackbar()
        }
        val totalBeats = (extendedContext.beatsInABar * extendedContext.barsToRecordFor) * 4 //number of 16th notes recorded
        val bucketsFinal = buckets.drop(4).subList(0, totalBeats)
        Log.d(logTag, "BUTTON INPUT FINSIHED, BUCKETS: $bucketsFinal")
        isRecording = false
        extendedContext.currentNoteData = bucketsFinal
        extendedContext.recordedBpm = extendedContext.bpm
    }

    //Metronome functions
    private suspend fun playNumBarsBlocking(bars: Int, bpm: Int, beatsInABar: Int, useVibrate: Boolean) = withContext(
        Dispatchers.Default) {
        val soundId = soundPool.load(context, R.raw.click, 1)
        val interval = 60000 / bpm
        val beats = bars * beatsInABar

        activity!!.runOnUiThread {
            binding.barNumDisplay.visibility = VISIBLE
            binding.barNumDisplay.text = getString(R.string.count_in)
        }

        for(i in 1..beats) {
            delay(interval.toLong())
            binding.countDisplay.text = i.toString()
            if(useVibrate) {
                v.vibrate(VibrationEffect.createOneShot(50, 100))
            } else {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1F)
            }

        }
    }

    private fun playNumBars(bars: Int, bpm: Int, beatsInABar: Int, useVibrate: Boolean) {
        val soundId = soundPool.load(context, R.raw.click, 1)
        val interval = 60000 / bpm
        val beats = bars * beatsInABar

        var displayVal = 1
        var barCount = 0

        metronomeJob = scope.launch {
            for(i in 1..beats) {
                delay(interval.toLong())

                if ((displayVal - 1) % beatsInABar == 0){
                    displayVal = 1
                    barCount++
                }
                activity!!.runOnUiThread {
                    binding.countDisplay.text = displayVal.toString()
                    binding.barNumDisplay.text = getString(R.string.bar, barCount)
                }

                displayVal++
                if (useVibrate) {
                    v.vibrate(VibrationEffect.createOneShot(50, 100))
                } else {
                    soundPool.play(soundId, 1f, 1f, 1, 0, 1F)
                }
            }
        }
        metronomeJob.start()
    }
}