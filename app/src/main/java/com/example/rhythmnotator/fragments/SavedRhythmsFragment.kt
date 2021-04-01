package com.example.rhythmnotator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rhythmnotator.activities.MainActivity
import com.example.rhythmnotator.adapters.SavedRhythmsListAdapter
import com.example.rhythmnotator.databinding.FragmentSavedRhythmsBinding

class SavedRhythmsFragment : Fragment() {

    private lateinit var binding: FragmentSavedRhythmsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedRhythmsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val files = context!!.fileList()

        binding.savedRhythmsList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SavedRhythmsListAdapter(files, activity!!.applicationContext,
                activity!! as MainActivity
            )
        }
    }
}