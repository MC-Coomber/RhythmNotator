package com.example.rhythmnotator

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Metronome(private val context: Context) {
    private val logTag = "METRONOME"
    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()
    private var id: Int = soundPool.load(context, R.raw.click, 1)
    private var isPlaying = false

    suspend fun playNumBarsBlocking(bars: Int, bpm: Int, beatsInABar: Int) {
        val interval = 60000 / bpm
        val beats = bars * beatsInABar
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        for(i in 1..beats) {
            delay(interval.toLong())
            soundPool.play(id, 1f, 1f, 1, 0, 1F)
//            v.vibrate(VibrationEffect.createOneShot(50, 100))
        }
    }

    fun playNumBars(bars: Int, bpm: Int, beatsInABar: Int ) {
        val interval = 60000 / bpm
        val beats = bars * beatsInABar
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        GlobalScope.launch {
            for(i in 1..beats) {
                delay(interval.toLong())
//                v.vibrate(VibrationEffect.createOneShot(50, 100))
                soundPool.play(id, 1f, 1f, 1, 0, 1F)
            }
        }
    }

    fun start(bpm: Int) {
        val interval = 60000 / bpm
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        isPlaying = true
        GlobalScope.launch {
            while (isPlaying) {
                delay(interval.toLong())
                if (isPlaying) {
//                    v.vibrate(VibrationEffect.createOneShot(50, 50))
                    soundPool.play(id, 1f, 1f, 1, 0, 1F)
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
    }
}