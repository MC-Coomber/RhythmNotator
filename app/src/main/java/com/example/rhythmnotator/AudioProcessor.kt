package com.example.rhythmnotator

import android.util.Log
import kotlin.math.abs

class AudioProcessor (private val audioData: ShortArray){

    private val sampleRate = MainActivity.sampleRate
    private val bpm = MainActivity.bpm
    private val barLength = MainActivity.beatsInABar
    private val threshold = 200
    private val logTag = "AUDIO"


    fun getNoteData() {
        Log.d(logTag, "bpm: $bpm sampleRate: $sampleRate")
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
        val buckets = processedAudio.chunked(sixteenthSampleLength)
        val totalBeats = (MainActivity.beatsInABar * MainActivity.barsToRecordFor) * 4 //number of 16th notes recorded
        return buckets.drop(4).subList(0, totalBeats)

    }

    //Converts buckets into boolean values where true is a note and false is a rest
    private fun parseBuckets(buckets: List<List<Int>>): ArrayList<Boolean> {
        var notes = ArrayList<Boolean>()
        for (bucket in buckets) {
            val average = bucket.average()
            Log.d(logTag, "bucket: $average")
            notes.add(average > threshold)
        }

        return notes
    }

}