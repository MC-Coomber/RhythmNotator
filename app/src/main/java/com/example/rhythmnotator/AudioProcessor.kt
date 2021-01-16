package com.example.rhythmnotator

import android.util.Log
import org.jtransforms.fft.FloatFFT_1D
import kotlin.math.abs


class AudioProcessor(private val audioBuffer: ShortArray, private val silenceBuffer: ShortArray){

    private val sampleRate = MainActivity.sampleRate
    private val bpm = MainActivity.bpm
    private val barLength = MainActivity.beatsInABar
    private val threshold = 1000
    private val logTag = "AUDIO"

    fun getNoteData() {
        //Fetch the frequency range of the silence recording and filter it out from the audio buffer
        val fftSilence = fourierTransform(silenceBuffer)
        val frequencyRange = getFrequencyRange(fftSilence)

        val fftAudioBuffer = fourierTransform(audioBuffer)
        Log.d(logTag, "before: ${fftAudioBuffer.contentToString()}")
        val fftSize = fftAudioBuffer.size/2
        filterFrequencyRange(frequencyRange[0], frequencyRange[1], fftSize, fftAudioBuffer)

        val processedAudio = processAudio(fftAudioBuffer)
        val buckets = createBuckets(processedAudio)
        val notes = parseBuckets(buckets)
        Log.d(logTag, "Note data: $notes")

    }

    private fun processAudio(audioBuffer: FloatArray): ArrayList<Int> {
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
        return buckets.dropLast(1)

    }

    //Converts buckets into boolean values where true is a note and false is a rest
    private fun parseBuckets(buckets: List<List<Int>>): ArrayList<Boolean> {
        var notes = ArrayList<Boolean>()
        Log.d(logTag, "num buckets: ${buckets.size}")
        for (bucket in buckets) {
            val average = bucket.average()
            Log.d(logTag, "bucket: $average")
            notes.add(average > threshold)
        }

        return notes
    }

    private fun fourierTransform(buffer: ShortArray): FloatArray {
        var fftResult = FloatArray(buffer.size)
        buffer.forEachIndexed { index, sh ->
            fftResult[index] = sh.toFloat()
        }
        val fftSize: Int = buffer.size / 2
        val mFFT = FloatFFT_1D(fftSize.toLong()) //this is a jTransforms type
        mFFT.realForward(fftResult)

        return fftResult
    }

    private fun getFrequencyRange(fft: FloatArray): IntArray {
        val maxFreqBin = fft.maxOrNull()!!
        val indexOfMax = fft.indexOfFirst {
            it == maxFreqBin
        }
        val maxFreq = (indexOfMax * MainActivity.sampleRate) / (fft.size / 2)

        val minFreqBin = fft.minOrNull()!!
        val indexOfMin = fft.indexOfFirst {
            it == minFreqBin
        }
        val minFreq = (indexOfMin * MainActivity.sampleRate) / (fft.size / 2)

        return intArrayOf(maxFreq, minFreq)
    }

    private fun filterFrequencyRange(maxFreq: Int, minFreq: Int, fftSize: Int, fft: FloatArray) {

        Log.d(logTag, "max freq: $maxFreq lowest: $minFreq")
        var maxFreqLocal = 0f
        for (fftBin in 0 until fftSize) {
            val frequency = fftBin.toFloat() * MainActivity.sampleRate / fftSize.toFloat()
            if (frequency > maxFreqLocal) {
                maxFreqLocal = frequency
            }
            if (frequency < maxFreq || frequency > minFreq) {

                val real = 2 * fftBin
                val imaginary = 2 * fftBin + 1

                //zero out this frequency
                fft[real] = 0F
                fft[imaginary] = 0F
            }
        }
        Log.d(logTag, "max freqeuncy in buffer: $maxFreqLocal")
        val mFFT = FloatFFT_1D(fftSize.toLong())
        mFFT.realInverse(fft, true)
    }

}