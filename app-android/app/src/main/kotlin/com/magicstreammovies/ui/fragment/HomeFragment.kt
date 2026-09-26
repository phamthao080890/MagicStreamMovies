package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.magicstreammovies.R
import com.magicstreammovies.ui.BaseMovieFragment
import com.magicstreammovies.viewmodel.MovieViewModel

class HomeFragment : BaseMovieFragment() {

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        initializeStateViews(inflater.inflate(R.layout.fragment_movie_list, viewGroup, false))

        val movieAdapter = createMovieAdapter()
        setupMovieGrid(rootView, movieAdapter)

        val movieViewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        movieViewModel.movies().observe(viewLifecycleOwner) { state ->
            renderMovieState(state, movieAdapter, getString(R.string.no_movies_available))
        }

        if (bundle == null) movieViewModel.loadMovies()

        return rootView
    }
}
