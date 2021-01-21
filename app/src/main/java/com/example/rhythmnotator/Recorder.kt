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

class Recorder(private val metronome: Metronome) {
    private lateinit var record: AudioRecord
    lateinit var audioBuffer: ShortArray

    private lateinit var silenceRecorder: AudioRecord
    lateinit var silenceBuffer: ShortArray

    private val logTag = "AUDIO"

    fun init() {
        var recordTime =
            (MainActivity.barsToRecordFor * MainActivity.beatsInABar) / (MainActivity.bpm / 60)
        var excessRecordTime: Float = (1 / (MainActivity.bpm / 60F)) * 2
        var totalRecordTime: Int = recordTime + excessRecordTime.roundToInt()
        Log.d(logTag, "Time: $recordTime Excess: $excessRecordTime")
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

        //Recording 'silence' for 1 bar
        val silenceRecordTime = MainActivity.beatsInABar / (MainActivity.bpm / 60)
        val silenceBufferSize = MainActivity.sampleRate * silenceRecordTime
        Log.d(logTag, "Count in time: $silenceRecordTime")
        silenceBuffer = ShortArray(bufferSize)
        silenceRecorder = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            MainActivity.sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            silenceBufferSize
        )
    }

    private fun countIn() {
        Log.d(logTag, "Start Count in")
        metronome.playNumBars(1)
        silenceRecorder.startRecording()
        var shortsRead: Long = 0
        while (shortsRead <= silenceBuffer.size / 2) {
            val numberOfShort = silenceRecorder.read(silenceBuffer, 0, silenceBuffer.size)
            shortsRead += numberOfShort.toLong()
        }
        silenceRecorder.stop()
        Log.d(logTag, "Count in stopped")
    }

    fun start() {
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
        val audioProcessor = AudioProcessor(audioBuffer, silenceBuffer)
        audioProcessor.getNoteData()
    }

}