package com.magicstreammovies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.data.repository.ApiResult
import com.magicstreammovies.data.repository.MovieRepository

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val movieState = MutableLiveData<ViewState<List<Movie>>>()

    private val detailState = MutableLiveData<ViewState<Movie>>()

    private val genreState = MutableLiveData<ViewState<List<Genre>>>()

    fun movies(): LiveData<ViewState<List<Movie>>> = movieState

    fun detail(): LiveData<ViewState<Movie>> = detailState

    fun genres(): LiveData<ViewState<List<Genre>>> = genreState

    private fun <T> post(target: MutableLiveData<ViewState<T>>, r: ApiResult<T>) {
        target.postValue(if (r.isSuccess) ViewState(data = r.data) else ViewState(error = r.error))
    }

    fun loadMovies() {
        movieState.value = ViewState(loading = true)
        repository.movies { post(movieState, it) }
    }

    fun search(query: String) {
        movieState.value = ViewState(loading = true)
        repository.search(query) {
            post(movieState, it)
        }
    }

    fun loadByGenre(name: String) {
        movieState.value = ViewState(loading = true)
        repository.moviesByGenre(name) {
            post(movieState, it)
        }
    }

    fun loadDetail(id: String) {
        detailState.value = ViewState(loading = true)
        repository.movie(id) {
            post(detailState, it)
        }
    }

    fun loadGenres() {
        genreState.value = ViewState(loading = true)
        repository.genres { post(genreState, it) }
    }
}
