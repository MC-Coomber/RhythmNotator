package com.example.rhythmnotator

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Playback(private val context: Context) {
    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()
    private val logTag = "PLAYBACK"

    fun playRhythm(bpm: Int, noteData: ArrayList<Boolean>) {
        val sixteenthNoteLengthMillis = (60000 / bpm) / 4
        val id = soundPool.load(context, R.raw.click, 1)
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                GlobalScope.launch {
                    noteData.forEach {
                        if (it) {
                            soundPool.play(id, 1f, 1f, 1, 0, 1F)
                        }
                        delay(sixteenthNoteLengthMillis.toLong())
                    }
                    soundPool.release()
                }
            }
        }
    }
}