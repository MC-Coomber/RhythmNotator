package com.example.rhythmnotator

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

class NoteRenderer (private val noteData: ArrayList<Boolean>, private val layout: LinearLayout, private val context: Context){

    fun renderNotes() {
        val quaterNotes = noteData.chunked(4)
        quaterNotes.forEach {
            layout.addView(getNoteImage(it))
        }
    }

    private fun getNoteImage(note: List<Boolean>): ImageView {
        val crotchet = listOf(true, false, false, false)
        val image = ImageView(context)
        if (note == crotchet) {

            image.setImageResource(R.drawable.crotchet)

        } else {
            image.setImageResource(R.drawable.crotchetrest)
        }
        return image
    }
}