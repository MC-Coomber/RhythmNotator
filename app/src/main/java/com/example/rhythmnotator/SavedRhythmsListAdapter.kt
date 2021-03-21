package com.example.rhythmnotator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedRhythmsListAdapter(private var rhythmNames: Array<String>?) : RecyclerView.Adapter<SavedRhythmsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.layout_saved_rhythm, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rhythmName = rhythmNames?.get(position)
        holder.rhythmNameText.text = rhythmName
    }

    override fun getItemCount(): Int {
        return rhythmNames!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rhythmNameText: TextView = itemView.findViewById(R.id.rhythm_name)
    }
}