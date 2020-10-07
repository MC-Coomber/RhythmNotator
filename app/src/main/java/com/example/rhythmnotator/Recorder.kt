package com.example.rhythmnotator

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import android.util.Log
import java.util.*

class Recorder {

    private var record: AudioRecord
    private var audioBuffer: ShortArray
    private val SAMPLE_RATE = 4000
    private var mShouldContinue = false
    private val LOG_TAG = "RECORDER"
    private val RECORD_TIME = 4

    init {
        // buffer size in bytes
        var bufferSize =  SAMPLE_RATE * RECORD_TIME

        audioBuffer = ShortArray(bufferSize)
        record = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!")
        }
    }

    fun start() {
        Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            record.startRecording()
            Log.v(LOG_TAG, "Start recording")
            var shortsRead: Long = 0
            mShouldContinue = true
            while (shortsRead <= audioBuffer.size) {
                val numberOfShort = record.read(audioBuffer, 0, audioBuffer.size)
                shortsRead += numberOfShort.toLong()
            }

            record.stop()
            Log.d(
                LOG_TAG,
                String.format(
                    "Recording stopped. data read: " + audioBuffer.contentToString()
                )
            )
        }).start()
    }

}