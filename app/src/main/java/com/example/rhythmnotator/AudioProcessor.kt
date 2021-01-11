package com.example.rhythmnotator

import android.util.Log
import kotlin.math.abs

class AudioProcessor (audioData: ShortArray, sampleRate: Int, bpm: Int){

    private val audioData = audioData
    private val sampleRate = sampleRate
    private val bpm = bpm
    private val barLength = 4
    private val threshold = 250
    private val logTag = "AUDIO"

    fun getNoteData() {
        val processedAudio = processAudio(audioData)
        val buckets = createBuckets(processedAudio)
        val notes = parseBuckets(buckets)
        Log.d(logTag, "Note data: $notes")
    }

    private fun processAudio(audioBuffer: ShortArray): ArrayList<Int> {
        var iterator = 0
        val noAverageSamples = 100
        var processedAudio = java.util.ArrayList<Int>()

        while(iterator < audioBuffer.size) {
            var newSample = abs(audioBuffer[iterator].toInt())
            if (iterator >= noAverageSamples) {
                var total = newSample
                for (i in 0..noAverageSamples) {
                    total += abs(audioBuffer[iterator - i].toInt())
                }
                newSample = total/noAverageSamples
                processedAudio.add(iterator, newSample)
            } else {
//                var total = newSample
//                for (i in 0..iterator) {
//                    total += abs(audioBuffer[iterator - i].toInt())
//                }
//                newSample = total/noAverageSamples
                processedAudio.add(iterator, newSample)
            }
            iterator++
        }
        Log.d(logTag, String.format("Processed sound =  $processedAudio"))
        return processedAudio
    }

    //Splits processed audio data into sixteenth note buckets
    private fun createBuckets(processedAudio: ArrayList<Int>): List<List<Int>> {
        val sixteenthSampleLength = 15 * (sampleRate/bpm)

        return processedAudio.chunked(sixteenthSampleLength)

    }

    //Converts buckets into boolean values where true is a note and false is a rest
    private fun parseBuckets(buckets: List<List<Int>>): ArrayList<Boolean> {
        var notes = ArrayList<Boolean>()
        for (bucket in buckets) {
            val average = bucket.average()
            notes.add(average > threshold)
        }

        return notes
    }

}