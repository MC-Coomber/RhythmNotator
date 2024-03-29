package com.example.rhythmnotator.utils

import android.util.Log
import kotlin.math.abs

class AudioProcessor (private val audioData: ShortArray, context: ExtendedContext){

    private val sampleRate = context.sampleRate
    private val bpm = context.bpm
    private val beatsInABar = context.beatsInABar
    private val barsToRecordFor = context.barsToRecordFor
    private val threshold = 200
    private val logTag = "AUDIO"


    fun getNoteData(): List<Boolean> {
        Log.d(logTag, "bpm: $bpm sampleRate: $sampleRate")
        val processedAudio = processAudio(audioData)
        val buckets = createBuckets(processedAudio)
        val notes = parseBuckets(buckets)
        Log.d(logTag, "Note data: $notes")

        return notes
    }

    private fun processAudio(audioBuffer: ShortArray): ArrayList<Int> {
        var iterator = 0
        val noAverageSamples = 100
        val processedAudio = java.util.ArrayList<Int>()

        while(iterator < audioBuffer.size) {
            //Find the absolute value of the sample
            var newSample = abs(audioBuffer[iterator].toInt())
            //If enough samples have been looked at, find the average of the last 100
            if (iterator >= noAverageSamples) {
                var total = newSample
                for (i in 0..noAverageSamples) {
                    total += abs(audioBuffer[iterator - i].toInt())
                }
                newSample = total/noAverageSamples
            }
            processedAudio.add(iterator, newSample)

            iterator++
        }

        return processedAudio
    }

    //Splits processed audio data into sixteenth note buckets
    private fun createBuckets(processedAudio: ArrayList<Int>): List<List<Int>> {
        val sixteenthSampleLength = 15 * (sampleRate/bpm)
        val buckets = processedAudio.chunked(sixteenthSampleLength)
        val totalBeats = (beatsInABar * barsToRecordFor) * 4 //number of 16th notes recorded

        return buckets.drop(4).subList(0, totalBeats)
    }

    //Converts buckets into boolean values where true is a note and false is a rest
    private fun parseBuckets(buckets: List<List<Int>>): List<Boolean> {
        val notes = ArrayList<Boolean>()
        for (bucket in buckets) {
            val average = bucket.average()
            notes.add(average > threshold)
        }

        return notes.toList()
    }

}