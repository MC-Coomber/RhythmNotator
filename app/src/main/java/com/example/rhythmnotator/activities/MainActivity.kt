package com.example.rhythmnotator.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.R
import com.example.rhythmnotator.fragments.PlaybackFragment
import com.example.rhythmnotator.fragments.RecordFragment
import com.example.rhythmnotator.fragments.SavedRhythmsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_record.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val logTag = "MAIN ACTIVITY"
    private var recording: ShortArray = ShortArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, RecordFragment()).commit()

        bottom_nav.setOnNavigationItemSelectedListener {
            var selectedFragment = Fragment()
            when(it.itemId) {
                R.id.record_item -> {
                    selectedFragment = RecordFragment()
                }
                R.id.playback_item -> {
                    selectedFragment = PlaybackFragment()
                }
                R.id.save_item -> {
                    selectedFragment = SavedRhythmsFragment()
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            return@setOnNavigationItemSelectedListener true
        }
    }
}

