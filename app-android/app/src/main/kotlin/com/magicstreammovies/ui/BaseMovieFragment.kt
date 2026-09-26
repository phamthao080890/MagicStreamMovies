package com.magicstreammovies.ui

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.magicstreammovies.R
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.ui.adapter.MovieAdapter
import com.magicstreammovies.ui.fragment.MovieDetailsFragment
import com.magicstreammovies.viewmodel.ViewState

open class BaseMovieFragment : BaseFragment() {
    protected fun createMovieAdapter() = MovieAdapter(::openMovieDetails)

    protected fun setupMovieGrid(root: View, adapter: MovieAdapter) {
        root.findViewById<RecyclerView>(R.id.movie_list).apply {
            layoutManager = GridLayoutManager(context, 2)
            this.adapter = adapter
        }
    }

    protected fun renderMovieState(state: ViewState<List<Movie>>, adapter: MovieAdapter, emptyMessage: String) {
        loadingProgressBar.visibility = if (state.loading) View.VISIBLE else View.GONE
        state.data?.let { movies ->
            adapter.submit(movies)
            messageTextView.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
            messageTextView.text = emptyMessage
        }
        state.error?.let {
            showMessage(it)
        }
    }

    protected fun showMessage(message: String?) {
        messageTextView.text = message
        messageTextView.visibility = View.VISIBLE
    }

    private fun openMovieDetails(movie: Movie) {
        movie.id?.let { id ->
            (requireActivity() as MainActivity).show(MovieDetailsFragment.create(id), movie.title.orEmpty())
        }
    }
}
