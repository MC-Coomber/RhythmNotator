package com.example.rhythmnotator.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rhythmnotator.ExtendedContext
import com.example.rhythmnotator.databinding.BottomDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BeatsPerBarDialogFragment(private val beatsPerBarDisplay: TextView) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val context = activity!!.applicationContext as ExtendedContext

        binding.bottomDialogTitle.text = "Beats Per Bar"
        binding.numPicker.minValue = 2
        binding.numPicker.maxValue = 16
        binding.numPicker.value = context.beatsInABar

        binding.numPicker.setOnValueChangedListener { _, _, newVal ->
            context.beatsInABar = newVal
        }

        binding.numBarDone.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val value = binding.numPicker.value
        beatsPerBarDisplay.text = value.toString()
        val context = activity!!.applicationContext as ExtendedContext
        context.beatsInABar = value
    }
}