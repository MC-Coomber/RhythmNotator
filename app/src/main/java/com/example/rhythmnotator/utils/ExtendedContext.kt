package com.example.rhythmnotator.utils

import android.app.Application

class ExtendedContext : Application() {
    private val defaultBpm = 120
    private val defaultBarsToRecordFor = 1
    private val defaultBeatsInABar = 4

    var bpm = defaultBpm
    var recordedBpm = defaultBpm
    var barsToRecordFor = defaultBarsToRecordFor
    var beatsInABar = defaultBeatsInABar
    var sampleRate =  44100

    var useTap = false
    var useMetronomeVibrate = false

    var currentNoteData = List<Boolean>(0) {
        true
    }

}