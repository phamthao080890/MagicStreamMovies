package com.magicstreammovies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.magicstreammovies.data.model.AuthResponse
import com.magicstreammovies.data.repository.ApiResult
import com.magicstreammovies.data.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authState = MutableLiveData<ViewState<AuthResponse>>()

    private val repository = AuthRepository()

    fun state(): LiveData<ViewState<AuthResponse>> = authState

    private fun post(result: ApiResult<AuthResponse>) {
        authState.postValue(if (result.isSuccess) ViewState(data = result.data) else ViewState(error = result.error))
    }

    fun login(email: String, password: String) {
        authState.value = ViewState(loading = true)
        repository.login(email, password, ::post)
    }

    fun register(name: String, email: String, password: String) {
        authState.value = ViewState(loading = true)
        repository.register(name, email, password, ::post)
    }
}
