package com.example.rhythmnotator

import android.media.*
import android.os.Process
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList
import kotlin.math.abs

class Recorder(private val metronome: Metronome) {
    private lateinit var record: AudioRecord
    lateinit var audioBuffer: ShortArray
    private val logTag = "AUDIO"

    fun init() {
        var recordTime =
            (MainActivity.barsToRecordFor * MainActivity.beatsInABar) / (MainActivity.bpm / 60)
        var excessRecordTime = (1 / (MainActivity.bpm / 60)) * 2
        var totalRecordTime: Int = recordTime + excessRecordTime
        var bufferSize = MainActivity.sampleRate * totalRecordTime

        audioBuffer = ShortArray(bufferSize)
        record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            MainActivity.sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(logTag, "Audio Record can't initialize!")
        }
    }

    private suspend fun countIn() {
        metronome.playNumBarsBlocking(1)
    }

    suspend fun start() {
        countIn()
        Log.d(logTag, "Start recording")
        metronome.playNumBars(MainActivity.barsToRecordFor)
        record.startRecording()
        var shortsRead: Long = 0
        while (shortsRead <= audioBuffer.size / 2) {
            val numberOfShort = record.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort.toLong()
        }
        record.stop()

        Log.d(
            logTag,
            String.format(
                "Recording stopped. data read:" +
                        " " + audioBuffer.contentToString()
            )
        )
        Log.d(
            logTag,
            String.format(
                "Max val:" + audioBuffer.maxOrNull() + " Min val:" + audioBuffer.minOrNull()
            )
        )
        val audioProcessor = AudioProcessor(audioBuffer)
        audioProcessor.getNoteData()
    }

}