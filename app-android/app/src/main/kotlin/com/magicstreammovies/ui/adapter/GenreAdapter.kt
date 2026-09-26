package com.magicstreammovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.magicstreammovies.R
import com.magicstreammovies.data.model.Genre

class GenreAdapter(private val click: (Genre) -> Unit) : Adapter<GenreAdapter.Holder>() {
    private val values = mutableListOf<Genre>()
    private var selected: String? = null

    fun submit(items: List<Genre>?) {
        values.clear()
        values.addAll(items.orEmpty())
        notifyDataSetChanged()
    }

    fun select(name: String?) {
        selected = name
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int) =
        Holder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_genre, viewGroup, false))

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(values[position])

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val label: TextView = view.findViewById(R.id.genre_label)

        fun bind(genre: Genre) {
            label.text = genre.name
            label.isSelected = genre.name.equals(
                selected,
                true
            )
            itemView.setOnClickListener {
                select(genre.name)
                click(genre)
            }
        }
    }
}
