package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magicstreammovies.R
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.data.repository.FavouriteRepository
import com.magicstreammovies.data.repository.MovieRepository
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.BaseMovieFragment
import java.util.concurrent.atomic.AtomicInteger

class FavouritesFragment : BaseMovieFragment() {

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        initializeStateViews(inflater.inflate(R.layout.fragment_movie_list, viewGroup, false))

        val movieAdapter = createMovieAdapter()
        setupMovieGrid(rootView, movieAdapter)

        FavouriteRepository().favourites(SessionManager(requireContext()).userId()!!) { result ->
            if (!result.isSuccess) {
                activity?.runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    messageTextView.text = result.error
                    messageTextView.visibility = View.VISIBLE
                }
            } else if (result.data.isNullOrEmpty()) {
                activity?.runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    messageTextView.text = getString(R.string.no_favourites)
                    messageTextView.visibility = View.VISIBLE
                }
            } else {
                val items = mutableListOf<Movie>()
                val done = AtomicInteger()
                result.data.forEach { id ->
                    MovieRepository().movie(id) { movie ->
                        movie.data?.let {
                            items.add(it)
                        }

                        if (done.incrementAndGet() == result.data.size) {
                            activity?.runOnUiThread {
                                loadingProgressBar.visibility = View.GONE
                                movieAdapter.submit(items)
                            }
                        }
                    }
                }
            }
        }
        return rootView
    }
}
