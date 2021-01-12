package com.example.rhythmnotator

import android.media.*
import android.os.Process
import android.util.Log
import java.util.ArrayList
import kotlin.math.abs

class Recorder(private val metronome: Metronome) {
    private lateinit var record: AudioRecord
    lateinit var audioBuffer: ShortArray
    private val logTag = "AUDIO"

    fun init() {
        val recordTime =
            (MainActivity.barsToRecordFor * MainActivity.beatsInABar) / (MainActivity.bpm / 60)

        // buffer size in bytes
        var bufferSize = MainActivity.sampleRate * recordTime
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

    fun start() {
        metronome.start()
        record.startRecording()
        Log.d(logTag, "Start recording")
        var shortsRead: Long = 0
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

        val audioProcessor = AudioProcessor(audioBuffer)
        audioProcessor.getNoteData()
    }

}