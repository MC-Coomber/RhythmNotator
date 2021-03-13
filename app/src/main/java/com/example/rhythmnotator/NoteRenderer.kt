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
import com.google.android.flexbox.*

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
        val barLayout = FlexboxLayout(context)
        val density = barLayout.context.resources.displayMetrics.density
        val barHeight = (60 * density).toInt()
        barLayout.apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, barHeight)
            justifyContent = JustifyContent.SPACE_EVENLY
            alignItems = AlignItems.BASELINE
        }

        val keySignature = ImageView(context)
        val keySignatureDensity = keySignature.context.resources.displayMetrics.density
        val params = FrameLayout.LayoutParams((keySignatureDensity * 100).toInt(), (keySignatureDensity * 100).toInt())
        keySignature.apply {
            setImageResource(R.drawable.ic_key_signature)
            layoutParams = params
        }

        val keySignatureLayout = LinearLayout(context)
        val keySignatureLayoutParams = FlexboxLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        keySignatureLayoutParams.apply {
            alignSelf = AlignItems.FLEX_END
            marginStart = (density * -20).toInt()
        }
        keySignatureLayout.apply {
            layoutParams = keySignatureLayoutParams
            addView(keySignature)
        }

        //Add the time signature if this bar is the first one
        if (isFirstBar) {
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
                marginStart = (density * -12).toInt()
                marginEnd = (density * 26).toInt()
            }
            timeSignatureContainer.apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = timeSigContainerParams
                addView(timeSignatureTextTop)
                addView(timeSignatureTextBottom)
            }

            keySignatureLayout.addView(timeSignatureContainer)
        }

        val parentLayout = LinearLayout(context)
        parentLayout.addView(keySignatureLayout)

        //Draw the notes
        val quarterDivisions = bar.chunked(4)
        quarterDivisions.forEach {
            barLayout.addView(getNoteImage(it, barLayout))
        }

        parentLayout.addView(barLayout)

        val baseLine = LinearLayout(context)
        val baseLineParams = FrameLayout.LayoutParams(MATCH_PARENT, (density * 1).toInt())
        baseLineParams.topMargin = (density * 50).toInt()
        baseLine.apply {
            layoutParams = baseLineParams
            setBackgroundColor(Color.parseColor("#000000"))
        }

        val barFrameLayout = FrameLayout(context)
        val barFrameLayoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        barFrameLayoutParams.bottomMargin = (density * 24).toInt()
        barFrameLayout.apply {
            addView(parentLayout)
            addView(baseLine)
            layoutParams = barFrameLayoutParams
        }

        return barFrameLayout
    }

    private fun getNoteImage(note: List<Boolean>, layout: FlexboxLayout): ImageView {
        val image = ImageView(context)
        val density = layout.context.resources.displayMetrics.density
        val params = LinearLayout.LayoutParams(WRAP_CONTENT, (density * 60).toInt())
//        params.marginEnd = (8 * density).toInt()
        image.layoutParams = params
        val imageResource = noteMap[note] ?: error("CANNOT FIND GIVEN NOTE")
        image.setImageResource(imageResource)

        return image
    }
}