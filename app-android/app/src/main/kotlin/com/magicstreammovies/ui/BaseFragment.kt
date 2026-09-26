package com.magicstreammovies.ui

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.magicstreammovies.R

open class BaseFragment : Fragment() {
    protected lateinit var rootView: View
    protected lateinit var loadingProgressBar: ProgressBar
    protected lateinit var messageTextView: TextView

    protected fun initializeStateViews(root: View) {
        rootView = root
        loadingProgressBar = root.findViewById(R.id.loading)
        messageTextView = root.findViewById(R.id.message)
    }
}
