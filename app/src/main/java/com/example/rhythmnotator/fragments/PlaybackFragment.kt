package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.ExtendedContext
import com.example.rhythmnotator.NoteRenderer
import com.example.rhythmnotator.Playback
import com.example.rhythmnotator.databinding.FragmentPlaybackBinding
import kotlinx.android.synthetic.main.fragment_playback.*

class PlaybackFragment : Fragment() {

    private lateinit var binding: FragmentPlaybackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentPlaybackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.playback.setOnClickListener {
            val playback = Playback(activity!!.applicationContext)
            val rhythm = arrayListOf(true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
                true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true)

            playback.playRhythm(120, rhythm)
        }

        binding.save.setOnClickListener {
            val dialog = SaveDialogFragment()
            dialog.show(activity!!.supportFragmentManager, "Dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        val noteRenderer = NoteRenderer(note_holder, activity!!.applicationContext)
        val context = activity!!.applicationContext as ExtendedContext
        val rhythm = arrayListOf(true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true,true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true,true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true,true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true)

        noteRenderer.renderNoteData(rhythm)
    }

}