package com.magicstreammovies.data

import android.widget.Button
import android.widget.EditText
import com.magicstreammovies.R
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.MainActivity
import com.magicstreammovies.ui.fragment.LoginFragment
import com.magicstreammovies.ui.fragment.RegisterFragment
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class FormValidationTest {
    private lateinit var controller: ActivityController<MainActivity>
    private lateinit var activity: MainActivity

    @Before fun setup() {
        controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        activity = controller.get()
        SessionManager(activity).clear()
        activity.supportFragmentManager.executePendingTransactions()
    }

    @After fun teardown() { controller.pause().stop().destroy() }

    @Test fun `featured rejects empty recommendation request`() {
        val query = activity.findViewById<EditText>(R.id.search_query)
        query.setText("   ")
        activity.findViewById<Button>(R.id.search_button).performClick()
        assertEquals(activity.getString(R.string.recommendation_required), query.error.toString())
    }

    @Test fun `login rejects invalid email`() {
        activity.showRoot(LoginFragment(), "Đăng nhập")
        activity.supportFragmentManager.executePendingTransactions()
        val email = activity.findViewById<EditText>(R.id.email)
        email.setText("invalid")
        activity.findViewById<Button>(R.id.login_button).performClick()
        assertEquals(activity.getString(R.string.valid_email), email.error.toString())
    }

    @Test fun `login requires password`() {
        activity.showRoot(LoginFragment(), "Đăng nhập")
        activity.supportFragmentManager.executePendingTransactions()
        activity.findViewById<EditText>(R.id.email).setText("an@example.com")
        val password = activity.findViewById<EditText>(R.id.password)
        activity.findViewById<Button>(R.id.login_button).performClick()
        assertEquals(activity.getString(R.string.password_required), password.error.toString())
    }

    @Test fun `registration requires name`() {
        activity.showRoot(RegisterFragment(), "Đăng ký")
        activity.supportFragmentManager.executePendingTransactions()
        val name = activity.findViewById<EditText>(R.id.name)
        activity.findViewById<Button>(R.id.register_button).performClick()
        assertEquals(activity.getString(R.string.name_required), name.error.toString())
    }

    @Test fun `registration rejects invalid email`() {
        activity.showRoot(RegisterFragment(), "Đăng ký")
        activity.supportFragmentManager.executePendingTransactions()
        activity.findViewById<EditText>(R.id.name).setText("An")
        val email = activity.findViewById<EditText>(R.id.email)
        email.setText("invalid")
        activity.findViewById<Button>(R.id.register_button).performClick()
        assertEquals(activity.getString(R.string.valid_email), email.error.toString())
    }

    @Test fun `registration requires password of at least six characters`() {
        activity.showRoot(RegisterFragment(), "Đăng ký")
        activity.supportFragmentManager.executePendingTransactions()
        activity.findViewById<EditText>(R.id.name).setText("An")
        activity.findViewById<EditText>(R.id.email).setText("an@example.com")
        val password = activity.findViewById<EditText>(R.id.password)
        password.setText("12345")
        activity.findViewById<Button>(R.id.register_button).performClick()
        assertEquals(activity.getString(R.string.password_length), password.error.toString())
    }
}
