package com.magicstreammovies.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.magicstreammovies.R
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.MainActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_profile, viewGroup, false)

        val session = SessionManager(requireContext())
        rootView.findViewById<TextView>(R.id.profile_name).text = session.name()
        rootView.findViewById<TextView>(R.id.profile_email).text = session.email()
        rootView.findViewById<Button>(R.id.logout_button).setOnClickListener {
            session.clear()
            (requireActivity() as MainActivity).refreshSessionNavigation()
            (requireActivity() as MainActivity).showRoot(
                LoginFragment.create(LoginFragment.DESTINATION_PROFILE),
                getString(R.string.sign_in)
            )
        }

        return rootView
    }
}
