package com.magicstreammovies.data.session

import android.content.Context
import androidx.core.content.edit
import com.magicstreammovies.data.model.AuthResponse

class SessionManager(context: Context) {
    private val preferences =
        context.getSharedPreferences("magicstream_session", Context.MODE_PRIVATE)

    fun save(user: AuthResponse, fallbackEmail: String) {
        val name = user.name?.takeIf { it.isNotBlank() } ?: fallbackEmail.substringBefore("@")
        preferences.edit {
            putString("user_id", user.userId)
                .putString("name", name)
                .putString("email", user.email ?: fallbackEmail)
        }
    }

    fun isLoggedIn() = userId() != null

    fun userId(): String? = preferences.getString("user_id", null)

    fun name(): String = preferences.getString("name", "") ?: ""

    fun email(): String = preferences.getString("email", "") ?: ""

    fun clear() = preferences.edit { clear() }
}
