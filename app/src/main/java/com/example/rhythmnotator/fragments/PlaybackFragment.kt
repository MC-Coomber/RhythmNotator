package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.Playback
import com.example.rhythmnotator.R

class PlaybackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playback, container, false)
    }

    fun onPlayClick(view: View) {
        val playback = Playback(activity!!.applicationContext)
        val rhythm = arrayListOf(true, true, true, true, true, false, false, false, true, false, false, false ,true, false, false, false)

        playback.playRhythm(200, rhythm)
    }
}