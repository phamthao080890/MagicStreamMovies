package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.magicstreammovies.R
import com.magicstreammovies.ui.BaseMovieFragment
import com.magicstreammovies.viewmodel.MovieViewModel

class SearchFragment : BaseMovieFragment() {

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        initializeStateViews(inflater.inflate(R.layout.fragment_search, viewGroup, false))

        val queryEditText = rootView.findViewById<EditText>(R.id.search_query)

        val movieAdapter = createMovieAdapter()
        setupMovieGrid(rootView, movieAdapter)

        val viewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        viewModel.movies().observe(viewLifecycleOwner) { state ->
            renderMovieState(state, movieAdapter, getString(R.string.no_search_results))
        }

        val searchMovies = {
            val text = queryEditText.text.toString().trim()
            if (text.isEmpty()) queryEditText.error = getString(R.string.movie_title_required) else viewModel.search(text)
        }
        rootView.findViewById<Button>(R.id.search_button).setOnClickListener {
            searchMovies()
        }

        queryEditText.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                searchMovies()
                true
            } else {
                false
            }
        }
        return rootView
    }
}
