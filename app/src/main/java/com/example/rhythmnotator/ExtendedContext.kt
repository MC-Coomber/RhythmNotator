package com.example.rhythmnotator

import android.app.Application

class ExtendedContext : Application() {
    private val defaultBpm = 60
    private val defaultBarsToRecordFor = 1
    private val defaultBeatsInABar = 4

    var bpm = defaultBpm
    var barsToRecordFor = defaultBarsToRecordFor
    var beatsInABar = defaultBeatsInABar
    var sampleRate =  44100

}