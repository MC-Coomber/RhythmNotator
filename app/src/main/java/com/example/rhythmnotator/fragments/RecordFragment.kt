package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.ExtendedContext
import com.example.rhythmnotator.R
import kotlinx.android.synthetic.main.fragment_record.*

class RecordFragment : Fragment() {
    private val logTag = "RECORD FRAGMENT"

    private var bpm = 60
    private var barsToRecordFor = 1
    private var beatsInABar = 4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onStart() {
        super.onStart()

        start.setOnClickListener {
            val inputBpm = bpm_input.text
            val inputNumBars = bar_num_input.text
            bpm = inputBpm.toString().toInt()
            barsToRecordFor = inputNumBars.toString().toInt()

            val context = activity!!.applicationContext as ExtendedContext
            context.bpm = bpm
            context.barsToRecordFor = barsToRecordFor

            val dialog = RecordDialog()
            dialog.show(activity!!.supportFragmentManager, "Dialog")
        }
    }

}