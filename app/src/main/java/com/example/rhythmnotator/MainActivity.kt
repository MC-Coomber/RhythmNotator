package com.example.rhythmnotator

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rhythmnotator.fragments.PlaybackFragment
import com.example.rhythmnotator.fragments.RecordFragment
import com.example.rhythmnotator.fragments.SavedFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

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
                    true
                }
                R.id.playback_item -> {
                    selectedFragment = PlaybackFragment()
                    true
                }
                R.id.save_item -> {
                    selectedFragment = SavedFragment()
                    true
                }
                else -> false
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            return@setOnNavigationItemSelectedListener true
        }
    }
}

