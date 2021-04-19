package com.example.rhythmnotator.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.rhythmnotator.R
import com.example.rhythmnotator.utils.ExtendedContext
import com.example.rhythmnotator.databinding.DialogSaveBinding

class SaveDialogFragment : DialogFragment() {
    private val logTag = "SAVE DIALOG"

    private lateinit var binding: DialogSaveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSaveBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.saveButton.setOnClickListener {
            //TODO: add validation

            val context = activity!!.applicationContext as ExtendedContext

            val fileName = binding.fileNameInput.editText?.text.toString()

            when {
                context.fileList().contains(fileName) -> {
                    binding.fileNameInput.isErrorEnabled = true
                    binding.fileNameInput.error = getString(R.string.rhytm_used_error)

                }
                fileName.contains('/') -> {
                    binding.fileNameInput.isErrorEnabled = true
                    binding.fileNameInput.error = getString(R.string.reserved_characters_error)

                }
                else -> {
                    val bpm = context.bpm.toString()
                    val beatsInABar = context.beatsInABar.toString()
                    val rhythm = context.currentNoteData

                    val fileContents = "$bpm;$beatsInABar;$rhythm"
                    Log.d(logTag, "file contents: $fileContents")
                    context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                        it.write(fileContents.toByteArray())
                    }

                    Log.d(logTag, "files: ${context.fileList().joinToString(",")}")

                    dismiss()
                }
            }

        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

}