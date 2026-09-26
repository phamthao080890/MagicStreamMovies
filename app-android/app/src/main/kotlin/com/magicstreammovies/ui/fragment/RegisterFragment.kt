package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magicstreammovies.R
import com.magicstreammovies.ui.MainActivity
import com.magicstreammovies.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_register, viewGroup, false)

        val nameEditText = rootView.findViewById<EditText>(R.id.name)
        val emailEditText = rootView.findViewById<EditText>(R.id.email)
        val passwordEditText = rootView.findViewById<EditText>(R.id.password)
        val messageTextView = rootView.findViewById<TextView>(R.id.message)

        val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        viewModel.state().observe(viewLifecycleOwner) { state ->
            state.error?.let {
                messageTextView.text = it
            }
            state.data?.let { messageTextView.text = getString(R.string.account_created) }
        }

        rootView.findViewById<Button>(R.id.register_button).setOnClickListener {
            if (nameEditText.text.isEmpty()) {
                nameEditText.error = getString(R.string.name_required)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.text).matches()) {
                emailEditText.error = getString(R.string.valid_email)
            } else if (passwordEditText.text.length < 6) {
                passwordEditText.error = getString(R.string.password_length)
            } else {
                viewModel.register(
                    nameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        rootView.findViewById<Button>(R.id.go_login).setOnClickListener {
            (requireActivity() as MainActivity).show(LoginFragment(), getString(R.string.sign_in))
        }

        return rootView
    }
}
