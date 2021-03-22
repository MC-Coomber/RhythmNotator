package com.example.rhythmnotator

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class SavedRhythmsListAdapter(private var rhythmNames: Array<String>, private val context: Context, private val actvity: Activity) : RecyclerView.Adapter<SavedRhythmsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.layout_saved_rhythm, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rhythmName = rhythmNames?.get(position)
        val file = File(context.filesDir, rhythmName)
        holder.rhythmNameText.text = rhythmName
        holder.deleteButton.setOnClickListener {
            val confirmationDialog = actvity.let {
                AlertDialog.Builder(it)
            }
            confirmationDialog.setMessage("Are you sure you want to delete this rhythm?")
            confirmationDialog.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _ ->
                dialogInterface.dismiss()
            }
            confirmationDialog.setPositiveButton("OK") { dialogInterface: DialogInterface, _ ->
                file.delete()
                dialogInterface.dismiss()
                update(context.fileList())
            }

            confirmationDialog.create()
            confirmationDialog.show()
        }
    }

    fun update(newData: Array<String>) {
        rhythmNames = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return rhythmNames!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rhythmNameText: TextView = itemView.findViewById(R.id.rhythm_name)
        val deleteButton: ImageView = itemView.findViewById(R.id.rhythm_delete)
    }
}