package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.example.rhythmnotator.R
import com.example.rhythmnotator.databinding.DialogSavedPlaybackBinding
import com.example.rhythmnotator.utils.NoteRenderer
import kotlinx.android.synthetic.main.dialog_saved_playback.*
import java.io.File

class SavedRhythmPlaybackDialog(private val rhythmFile: File): DialogFragment() {
    private val logTag = "SAVED RHYTHM PLAYBACK"

    lateinit var binding: DialogSavedPlaybackBinding
    private val rhythm: ArrayList<Boolean> = ArrayList<Boolean>()
    private var bpm: Int = 0
    private var beatsInABar: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSavedPlaybackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appbar.apply {
            setNavigationOnClickListener { v -> dismiss() }
            title = "Some Title"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val bufferedReader = rhythmFile.bufferedReader()
        val fileContents = bufferedReader.use {
            it.readText()
        }
        val contentsArray = fileContents.split(";")
        bpm = contentsArray[0].toInt()
        beatsInABar = contentsArray[1].toInt()

        val rhythmStringArray = contentsArray[2].split(",")
        rhythmStringArray.forEachIndexed { index, s ->
            var string = s
            string = string.drop(1)
            if (index == rhythmStringArray.size - 1) {
                string = string.dropLast(1)
            }
            Log.d(logTag, string)
            rhythm.add(string.toBoolean())
        }

        Log.d(logTag, "bpm:$bpm beats in a bar:$beatsInABar rhythm: ${rhythm.joinToString(",")}")

        val noteRenderer = NoteRenderer(binding.noteHolder, activity!!.applicationContext)
        noteRenderer.renderNoteData(rhythm.toList())
    }
}