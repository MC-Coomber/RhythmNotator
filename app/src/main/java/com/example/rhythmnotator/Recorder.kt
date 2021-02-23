package com.example.rhythmnotator

import android.media.*
import android.os.Process
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt

class Recorder(private val context: ExtendedContext) {
    private lateinit var record: AudioRecord
    private lateinit var audioBuffer: ShortArray
    private val logTag = "AUDIO"
    private var isRecording = false

    fun init() {
        var recordTime =
            (context.barsToRecordFor * context.beatsInABar) / (context.bpm / 60)
        var excessRecordTime: Float = (1 / (context.bpm / 60F)) * 2
        var totalRecordTime: Int = recordTime + excessRecordTime.roundToInt()
        Log.d(logTag, "Time: $recordTime Excess: $excessRecordTime")
        var bufferSize = context.sampleRate * totalRecordTime

        audioBuffer = ShortArray(bufferSize)
        record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            context.sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(logTag, "Audio Record can't initialize!")
        }
    }

    fun start(): ShortArray {
        Log.d(logTag, "Start recording")
        record.startRecording()
        var shortsRead: Long = 0
        isRecording = true
        while (shortsRead <= audioBuffer.size / 2 && isRecording) {
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


        return if (isRecording) {
            audioBuffer
        } else {
            //If recording is cancelled
            Log.d(logTag, "Recording Cancelled")
            ShortArray(0)
        }
    }

    fun stop() {
        isRecording = false
    }

}