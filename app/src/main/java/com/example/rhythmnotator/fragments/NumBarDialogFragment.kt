package com.example.rhythmnotator.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.rhythmnotator.ExtendedContext
import com.example.rhythmnotator.databinding.DialogBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NumBarDialogFragment(private val numBarDisplay: TextView) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogBottomBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val context = activity!!.applicationContext as ExtendedContext

        binding.bottomDialogTitle.text = "Bars to Record For"
        binding.numPicker.minValue = 1
        binding.numPicker.maxValue = 10
        binding.numPicker.value = context.barsToRecordFor

        binding.numPicker.setOnValueChangedListener { _, _, newVal ->
            context.barsToRecordFor = newVal
        }

        binding.numBarDone.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val value = binding.numPicker.value
        numBarDisplay.text = value.toString()
        val context = activity!!.applicationContext as ExtendedContext
        context.barsToRecordFor = value
    }
}