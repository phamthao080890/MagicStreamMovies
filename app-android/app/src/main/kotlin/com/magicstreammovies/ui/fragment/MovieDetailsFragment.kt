package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.magicstreammovies.R
import com.magicstreammovies.data.repository.FavouriteRepository
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.BaseFragment
import com.magicstreammovies.ui.MainActivity
import com.magicstreammovies.viewmodel.MovieViewModel

class MovieDetailsFragment : BaseFragment() {
    private var isFavourite = false
    companion object {
        fun create(id: String) = MovieDetailsFragment().apply { arguments = Bundle().apply { putString("movieId", id) } }
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        initializeStateViews(inflater.inflate(R.layout.fragment_movie_details, viewGroup, false))

        val movieId = requireArguments().getString("movieId")!!

        val favouriteButton = rootView.findViewById<Button>(R.id.favourite_button)
        val trailerButton = rootView.findViewById<Button>(R.id.trailer_button)
        val trailerContainer = rootView.findViewById<View>(R.id.trailer_container)
        val trailerPlayer = rootView.findViewById<WebView>(R.id.trailer_player)
        trailerContainer.visibility = View.GONE
        trailerPlayer.settings.javaScriptEnabled = true
        trailerPlayer.settings.domStorageEnabled = true
        trailerPlayer.webViewClient = WebViewClient()

        val session = SessionManager(requireContext())

        fun updateFavouriteButton() {
            favouriteButton.text = getString(if (isFavourite) R.string.remove_from_favourites else R.string.add_to_favourites)
        }

        updateFavouriteButton()
        if (session.isLoggedIn()) {
            FavouriteRepository().favourites(session.userId()!!) { result ->
                if (result.isSuccess) {
                    isFavourite = result.data.orEmpty().contains(movieId)
                    activity?.runOnUiThread { updateFavouriteButton() }
                }
            }
        }

        val viewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        viewModel.detail().observe(viewLifecycleOwner) { state ->
            loadingProgressBar.visibility = if (state.loading) View.VISIBLE else View.GONE

            state.data?.let { movie ->
                rootView.findViewById<TextView>(R.id.title).text = movie.title
                rootView.findViewById<TextView>(R.id.genres).text = getString(R.string.genre_format, movie.genreText())
                rootView.findViewById<TextView>(R.id.review).text = getString(R.string.review_format, movie.adminReview ?: getString(R.string.not_available))
                rootView.findViewById<TextView>(R.id.ranking).text = getString(R.string.ranking_format, movie.rankingText())

                Glide.with(this).load(movie.posterPath).into(rootView.findViewById(R.id.poster))

                trailerButton.visibility = if (movie.youtubeId.isNullOrEmpty()) View.GONE else View.VISIBLE
                trailerButton.setOnClickListener {
                    trailerPlayer.visibility = View.VISIBLE
                    trailerContainer.visibility = View.VISIBLE
                    trailerPlayer.loadDataWithBaseURL(
                        "https://www.youtube-nocookie.com",
                        """<html><body style="margin:0;background:#000;"><iframe width="100%" height="100%" src="https://www.youtube-nocookie.com/embed/${movie.youtubeId}?playsinline=1" frameborder="0" allow="accelerometer; autoplay; encrypted-media; picture-in-picture" allowfullscreen></iframe></body></html>""",
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            }
            state.error?.let {
                messageTextView.text = it
                messageTextView.visibility = View.VISIBLE
            }
        }

        favouriteButton.setOnClickListener {
            if (!session.isLoggedIn()) {
                (requireActivity() as MainActivity).show(LoginFragment(), getString(R.string.sign_in))
            } else {
                favouriteButton.isEnabled = false
                val repository = FavouriteRepository()
                val onComplete: (com.magicstreammovies.data.repository.ApiResult<Map<String, String>>) -> Unit = { result ->
                    activity?.runOnUiThread {
                        favouriteButton.isEnabled = true
                        if (result.isSuccess) {
                            isFavourite = !isFavourite
                            updateFavouriteButton()
                        }
                        messageTextView.text = if (result.isSuccess) {
                            getString(if (isFavourite) R.string.favourite_added else R.string.favourite_removed)
                        } else {
                            result.error
                        }
                        messageTextView.visibility = View.VISIBLE
                    }
                }
                if (isFavourite) {
                    repository.remove(session.userId()!!, movieId, onComplete)
                } else {
                    repository.add(session.userId()!!, movieId, onComplete)
                }
            }
        }
        viewModel.loadDetail(movieId)

        return rootView
    }
}
