package com.example.rhythmnotator

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class Metronome(context: Context) {
    private val logTag = "METRONOME"
    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()
    private var id: Int
    private var isPlaying = false

    init {
        id = soundPool.load(context, R.raw.click, 1)
    }

    fun start() {
        val interval = 60000 / MainActivity.bpm

        isPlaying = true
        GlobalScope.launch {
            while (isPlaying) {
                delay(interval.toLong())
                if (isPlaying) {
                    soundPool.play(id, 1f, 1f, 1, 0, 1F)
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
    }
}