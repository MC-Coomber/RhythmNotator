package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.ExtendedContext
import com.example.rhythmnotator.NoteRenderer
import com.example.rhythmnotator.Playback
import com.example.rhythmnotator.R
import kotlinx.android.synthetic.main.fragment_playback.*

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
        val rhythm = arrayListOf(true, false, false, false, true, false, false, false, true, false, false, false ,true, false, false, false)

        playback.playRhythm(200, rhythm)
    }

    override fun onResume() {
        super.onResume()
        val noteRenderer = NoteRenderer(note_holder, activity!!.applicationContext)
        val context = activity!!.applicationContext as ExtendedContext
        val rhythm = arrayListOf(true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false, true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false)

        noteRenderer.renderNoteData(rhythm)
    }

}