package com.example.marvelapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class MarvelCharacter(
    val imageUrl: String,
    val name: String,
    val date: String
)


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val marvelImage: ImageView
    val characterNameTextView: TextView = view.findViewById(R.id.name)
    val characterDateTextView: TextView = view.findViewById(R.id.date)

    init {
        // Find our RecyclerView item's ImageView for future use
        marvelImage = view.findViewById(R.id.marvelImage)
    }
}

class MarvelAdapter(private val marvelList: MutableList<MarvelCharacter>) : RecyclerView.Adapter<ViewHolder>() {

    // autogen func
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = marvelList[position]
        Glide.with(holder.itemView)
            .load(character.imageUrl)
            .centerCrop()
            .into(holder.marvelImage)

        holder.characterNameTextView.text = character.name
        holder.characterDateTextView.text = character.date
    }

    override fun getItemCount() = marvelList.size
}