package com.example.rhythmnotator

import android.media.*
import android.os.Process
import android.util.Log
import java.util.ArrayList
import kotlin.math.abs

class Recorder(private val metronome: Metronome) {

    var record: AudioRecord
    var audioBuffer: ShortArray
    private val sampleRate = MainActivity.sampleRate
    private val logTag = "AUDIO"
    //Recording time in seconds
    private val recordTime =
        (MainActivity.barsToRecordFor * MainActivity.beatsInABar) / (MainActivity.bpm / 60)

    init {
        // buffer size in bytes
        var bufferSize =  (sampleRate * recordTime) / 2
        Log.d(logTag, "buffer size: $bufferSize time to record for: $recordTime")
        audioBuffer = ShortArray(bufferSize)
        record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(logTag, "Audio Record can't initialize!")
        }
    }

    fun start() {
        record.startRecording()
        metronome.start()
        Log.d(logTag, "Start recording")
        var shortsRead: Long = 0
        while (shortsRead <= audioBuffer.size) {
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

        val audioProcessor = AudioProcessor(audioBuffer, sampleRate, 80)
        audioProcessor.getNoteData()
    }

}