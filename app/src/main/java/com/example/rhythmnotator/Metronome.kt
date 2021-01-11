package com.example.rhythmnotator

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class Metronome(context: Context) {
    private var soundPool: SoundPool
    private var id: Int
    private val bpm = MainActivity.RecordingConfig.bpm
    private val interval = 60000 / bpm
    private var isPlaying = false

    init {
        soundPool = SoundPool.Builder()
            .setMaxStreams(4) // to prevent delaying the next tick under any circumstances
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        id = soundPool.load(context, R.raw.click, 1)
    }

    fun start() {
        isPlaying = true
        GlobalScope.launch {
            while (isPlaying) {
                delay(interval.toLong())
                soundPool.play(id, 1f, 1f, 1, 0, 1F)
            }
        }
    }

    fun stop() {
        isPlaying = false
    }
}