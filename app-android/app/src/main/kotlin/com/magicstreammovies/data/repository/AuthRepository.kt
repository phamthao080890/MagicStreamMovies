package com.magicstreammovies.data.repository

import com.magicstreammovies.data.api.ApiClient
import com.magicstreammovies.data.model.AuthResponse
import com.magicstreammovies.data.model.LoginRequest
import com.magicstreammovies.data.model.RegisterRequest

class AuthRepository : BaseRepository() {
    fun login(
        email: String,
        password: String,
        callback: ResultCallback<AuthResponse>
    ) = execute(ApiClient.service.login(LoginRequest(email, password)), callback)

    fun register(
        name: String,
        email: String,
        password: String,
        callback: ResultCallback<AuthResponse>
    ) = execute(ApiClient.service.register(RegisterRequest(name, email, password)), callback)
}
