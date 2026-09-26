package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magicstreammovies.R
import com.magicstreammovies.ui.BaseMovieFragment
import com.magicstreammovies.ui.adapter.GenreAdapter
import com.magicstreammovies.viewmodel.MovieViewModel

class GenresFragment : BaseMovieFragment() {

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        initializeStateViews(inflater.inflate(R.layout.fragment_genres, viewGroup, false))

        val movieViewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        val genreAdapter = GenreAdapter {
            movieViewModel.loadByGenre(it.name ?: "")
        }

        val movieAdapter = createMovieAdapter()
        setupMovieGrid(rootView, movieAdapter)

        rootView.findViewById<RecyclerView>(R.id.genre_list).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = genreAdapter
        }

        movieViewModel.genres().observe(viewLifecycleOwner) { viewState ->
            viewState.data?.let {
                genreAdapter.submit(it)
                it.firstOrNull()?.let { genre ->
                    genreAdapter.select(genre.name)
                    movieViewModel.loadByGenre(genre.name ?: "")
                }
            }
        }

        movieViewModel.movies().observe(viewLifecycleOwner) { state ->
            renderMovieState(state, movieAdapter, getString(R.string.no_movies_in_genre))
        }

        if (bundle == null) movieViewModel.loadGenres()

        return rootView
    }
}
