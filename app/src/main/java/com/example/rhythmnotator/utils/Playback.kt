package com.example.rhythmnotator.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.rhythmnotator.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Playback(private val context: Context) {

    private val logTag = "PLAYBACK"
    private var isPlaying = true

    fun playRhythm(bpm: Int, noteData: List<Boolean>, onComplete: () -> Unit) {
        val sixteenthNoteLengthMillis = (60000 / bpm) / 4
        val soundPool: SoundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        val id = soundPool.load(context, R.raw.click, 1)
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                GlobalScope.launch {
                    var i = 0
                    isPlaying = true
                    while(isPlaying && i < noteData.size) {
                        if (noteData[i]) {
                            soundPool.play(id, 1f, 1f, 1, 0, 1F)
                        }
                        delay(sixteenthNoteLengthMillis.toLong())
                        i++
                    }
                    soundPool.release()
                    onComplete()
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
    }
}