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

    private lateinit var silenceRecorder: AudioRecord
    lateinit var silenceBuffer: ShortArray

    private val logTag = "AUDIO"

    fun init() {
        val recordTime =
            (MainActivity.barsToRecordFor * MainActivity.beatsInABar) / (MainActivity.bpm / 60)

        val bufferSize = MainActivity.sampleRate * recordTime
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
        silenceBuffer = ShortArray(bufferSize)
        silenceRecorder = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            MainActivity.sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            silenceBufferSize
        )
    }

    suspend fun countIn() {
        metronome.playNumBars(1)
        silenceRecorder.startRecording()
        var shortsRead: Long = 0
        while (shortsRead <= silenceBuffer.size / 2) {
            val numberOfShort = silenceRecorder.read(silenceBuffer, 0, silenceBuffer.size)
            shortsRead += numberOfShort.toLong()
        }
        silenceRecorder.stop()
    }

    suspend fun start() {
        countIn()
        Log.d(logTag, "Start recording")
        record.startRecording()
        var shortsRead: Long = 0
        metronome.start()
        while (shortsRead <= audioBuffer.size / 2) {
            val numberOfShort = record.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort.toLong()
        }
        record.stop()
        metronome.stop()

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
                "Max val:" + audioBuffer.max() + " Min val:" + audioBuffer.min()
            )
        )

        val audioProcessor = AudioProcessor(audioBuffer, silenceBuffer)
        audioProcessor.getNoteData()
    }

}