package com.example.rhythmnotator

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class NoteRenderer (private val layout: LinearLayout, private val context: Context){
    private val logTag = "NOTE RENDERER"

    private val noteMap = mapOf(
        listOf(false, false, false, false) to R.drawable.ffff,
        listOf(true, false, false, false) to R.drawable.ic_tfff,
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

    fun renderNoteData(noteData: List<Boolean>) {
        val extendedContext = context as ExtendedContext

        val bucketsPerBar = extendedContext.beatsInABar * 4
        val bars = noteData.chunked(bucketsPerBar)
        layout.removeAllViews()

        bars.forEachIndexed { index, list ->
            if (index == 0) {
                layout.addView(renderBar(list, isFirstBar = true))
            } else {
                layout.addView(renderBar(list, false))
            }
        }
    }

    private fun renderBar(bar: List<Boolean>, isFirstBar: Boolean): FrameLayout {
        val quarterDivisions = bar.chunked(4)
        val barLayout = LinearLayout(context)

        val density = barLayout.context.resources.displayMetrics.density
        val barHeight = (138 * density).toInt()

        barLayout.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, barHeight)
        barLayout.orientation = LinearLayout.HORIZONTAL

        if (isFirstBar) {
            val stave = ImageView(context)
            val staveDensity = stave.context.resources.displayMetrics.density
            val params = FrameLayout.LayoutParams((staveDensity * 100).toInt(), (staveDensity * 100).toInt())
            stave.apply {
                setImageResource(R.drawable.ic_stave)
                layoutParams = params
            }

            val timeSignatureTextTop = TextView(context)
            timeSignatureTextTop.apply {
                text = (context as ExtendedContext).beatsInABar.toString()
                setTextAppearance(R.style.TimeSignatureText)
            }

            val timeSignatureTextBottom = TextView(context)
            val timeSigTextBottomParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            timeSigTextBottomParams.topMargin = (density * -8).toInt()
            timeSignatureTextBottom.apply {
                text = "4"
                setTextAppearance(R.style.TimeSignatureText)
                layoutParams = timeSigTextBottomParams
            }

            val timeSignatureContainer = LinearLayout(context)
            val timeSigContainerParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            timeSigContainerParams.apply {
                topMargin = (density * 10).toInt()
                marginStart = (density * 50).toInt()
            }
            timeSignatureContainer.apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = timeSigContainerParams
                addView(timeSignatureTextTop)
                addView(timeSignatureTextBottom)
            }

            val staveLayout = FrameLayout(context)
            val staveLayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            staveLayoutParams.gravity = Gravity.BOTTOM
            staveLayout.apply {
                layoutParams = staveLayoutParams
                addView(stave)
                addView(timeSignatureContainer)
            }

            barLayout.addView(staveLayout)
        }

        quarterDivisions.forEach {
            barLayout.addView(getNoteImage(it, barLayout))
        }

        val frameLayout = FrameLayout(context)
        frameLayout.addView(barLayout)

        val baseLine = LinearLayout(context)
        val baseLineParams = FrameLayout.LayoutParams(MATCH_PARENT, (density).toInt())
        baseLineParams.topMargin = (barHeight / 2 + (density * 18)).toInt()
        baseLine.apply {
            layoutParams = baseLineParams
            setBackgroundColor(Color.parseColor("#000000"))
        }
        frameLayout.addView(baseLine)

        return frameLayout
    }

    private fun getNoteImage(note: List<Boolean>, layout: LinearLayout): ImageView {
        val image = ImageView(context)
        val density = layout.context.resources.displayMetrics.density
        val params = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        params.marginEnd = (24 * density).toInt()
        image.layoutParams = params
        val imageResource = noteMap[note] ?: error("CANNOT FIND GIVEN NOTE")
        image.setImageResource(imageResource)

        return image
    }
}