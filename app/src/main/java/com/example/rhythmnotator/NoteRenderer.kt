package com.example.rhythmnotator

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout

class NoteRenderer (private val layout: LinearLayout, private val context: Context){

    private val noteMap = mapOf(
        listOf(false, false, false, false) to R.drawable.ffff,
        listOf(true, false, false, false) to R.drawable.tfff,
        listOf(false, true, false, false) to R.drawable.ftff,
        listOf(false, false, true, false) to R.drawable.fftf,
        listOf(false, false, false, true) to R.drawable.ffft,
        listOf(true, true, false, false) to R.drawable.ttff,
        listOf(true, false, true, false) to R.drawable.tftf,
        listOf(true, false, false, true) to R.drawable.tfft,
        listOf(false, true, true, false) to R.drawable.fttf,
        listOf(false, true, false, true) to R.drawable.ftft,
        listOf(false, false, true, true) to R.drawable.fftt,
        listOf(true, true, true, false) to R.drawable.tttf,
        listOf(true, false, true, true) to R.drawable.tftt,
        listOf(true, true, false, true) to R.drawable.ttft,
        listOf(false, true, true, true) to R.drawable.fttt,
        listOf(true, true, true, true) to R.drawable.tttt
    )

    fun renderNoteData(noteData: ArrayList<Boolean>) {
        val bucketsPerBar = MainActivity.beatsInABar * 4
        val bars = noteData.chunked(bucketsPerBar)
        layout.removeAllViews()
        bars.forEach {
            layout.addView(renderBar(it))
        }

    }

    private fun renderBar(bar: List<Boolean>): LinearLayout {
        val quarterNotes = bar.chunked(4)
        val barLayout = LinearLayout(context)
        barLayout.orientation = LinearLayout.HORIZONTAL

        quarterNotes.forEach {
            barLayout.addView(getNoteImage(it))
        }

        return barLayout
    }

    private fun getNoteImage(note: List<Boolean>): ImageView {
        val image = ImageView(context)
        val imageResource = noteMap[note] ?: error("CANNOT FIND GIVEN NOTE")
        image.setImageResource(imageResource)

        return image
    }
}