package com.example.rhythmnotator.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.R
import com.example.rhythmnotator.databinding.ActivityMainBinding
import com.example.rhythmnotator.fragments.PlaybackFragment
import com.example.rhythmnotator.fragments.RecordFragment
import com.example.rhythmnotator.fragments.SavedRhythmsFragment

class MainActivity : AppCompatActivity() {
    private val logTag = "MAIN ACTIVITY"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, RecordFragment()).commit()

        binding.bottomNav.setOnNavigationItemSelectedListener {
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

    fun switchToPlayback() {
        binding.bottomNav.selectedItemId = R.id.playback_item
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PlaybackFragment()).commit()
    }
}

