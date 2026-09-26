package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.magicstreammovies.R
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.BaseFragment
import com.magicstreammovies.ui.MainActivity
import com.magicstreammovies.viewmodel.AuthViewModel

class LoginFragment : BaseFragment() {
    companion object {
        const val DESTINATION_HOME = "home"
        const val DESTINATION_PROFILE = "profile"
        const val DESTINATION_FAVOURITES = "favourites"
        private const val ARG_DESTINATION = "destination"

        fun create(destination: String) = LoginFragment().apply {
            arguments = Bundle().apply { putString(ARG_DESTINATION, destination) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        initializeStateViews(inflater.inflate(R.layout.fragment_login, viewGroup, false))

        val emailEditText = rootView.findViewById<EditText>(R.id.email)
        val passwordEditText = rootView.findViewById<EditText>(R.id.password)

        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        viewModel.state().observe(viewLifecycleOwner) { state ->
            loadingProgressBar.visibility = if (state.loading) View.VISIBLE else View.GONE
            state.error?.let {
                messageTextView.text = it
            }
            state.data?.let {
                SessionManager(requireContext()).save(it, emailEditText.text.toString())
                (requireActivity() as MainActivity).refreshSessionNavigation()
                (requireActivity() as MainActivity).showAfterLogin(
                    arguments?.getString(ARG_DESTINATION) ?: DESTINATION_HOME
                )
            }
        }

        rootView.findViewById<Button>(R.id.login_button).setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text).matches()) {
                emailEditText.error = getString(R.string.valid_email)
            } else if (passwordEditText.text.isEmpty()) {
                passwordEditText.error = getString(R.string.password_required)
            } else {
                viewModel.login(emailEditText.text.toString(), passwordEditText.text.toString())
            }
        }

        rootView.findViewById<Button>(R.id.go_register).setOnClickListener {
            (requireActivity() as MainActivity).show(RegisterFragment(), getString(R.string.register))
        }

        return rootView
    }
}
