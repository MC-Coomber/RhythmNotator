package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.databinding.FragmentPlaybackBinding
import com.example.rhythmnotator.utils.ExtendedContext
import com.example.rhythmnotator.utils.NoteRenderer
import com.example.rhythmnotator.utils.Playback

class PlaybackFragment : Fragment() {

    private lateinit var binding: FragmentPlaybackBinding
    private lateinit var playback: Playback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaybackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        playback = Playback(activity!!.applicationContext)
        var isPlaying = false
        binding.play.setOnClickListener {
            val rhythm = arrayListOf(true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
                true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true)
            val onComplete = {
                isPlaying = false
            }

            if (!isPlaying) {
                isPlaying = true
                playback.playRhythm(120, rhythm, onComplete)

            }
        }

        binding.stop.setOnClickListener {
            playback.stop()
        }

        binding.save.setOnClickListener {
            val dialog = SaveDialogFragment()
            dialog.show(activity!!.supportFragmentManager, "Dialog")
        }
    }

    override fun onResume() {
        super.onResume()
        val noteRenderer = NoteRenderer(binding.noteHolder, activity!!.applicationContext)
        val context = activity!!.applicationContext as ExtendedContext
        val rhythm = arrayListOf(true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true,true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true,true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true,true, false, true, false, true, false, false, false, true, false, false, false ,true, false, false, false,
            true, true, true, true, true, true, true, true, true, false, true, true ,true, false, true, true)

        //TODO: pass actual bpm to note renderer
        noteRenderer.renderNoteData(rhythm, 120)
    }

    override fun onPause() {
        super.onPause()
        playback.stop()
    }

}