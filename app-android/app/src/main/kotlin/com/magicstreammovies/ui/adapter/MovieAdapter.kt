package com.magicstreammovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.magicstreammovies.R
import com.magicstreammovies.data.model.Movie

class MovieAdapter(private val click: (Movie) -> Unit) : Adapter<MovieAdapter.Holder>() {
    private val values = mutableListOf<Movie>()

    fun submit(items: List<Movie>?) {
        values.clear()
        values.addAll(items.orEmpty())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int) =
        Holder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_movie, viewGroup, false))

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(values[position])

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val poster: ImageView = view.findViewById(R.id.movie_poster)
        private val title: TextView = view.findViewById(R.id.movie_title)

        fun bind(movie: Movie) {
            title.text = movie.title
            Glide.with(poster)
                .load(movie.posterPath)
                .centerCrop()
                .placeholder(R.drawable.poster_placeholder)
                .into(poster)
            itemView.setOnClickListener {
                click(movie)
            }
        }
    }
}
