package de.cronosx.mumblebot

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import de.cronosx.mumblebot.api.Api
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class LoginActivity : AppCompatActivity() {
    val api = Api()

    var viewUsername: AutoCompleteTextView? = null
    var viewPassword: EditText? = null
    var viewProgress: View? = null
    var viewLoginForm: View? = null
    var viewSignInButton: Button? = null

    var inProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Find all views.
        viewSignInButton = findViewById(R.id.sign_in_button) as Button
        viewLoginForm = findViewById(R.id.login_form)
        viewProgress = findViewById(R.id.login_progress)
        viewUsername = findViewById(R.id.username) as AutoCompleteTextView
        viewPassword = findViewById(R.id.password) as EditText

        viewPassword!!.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        viewSignInButton!!.setOnClickListener { attemptLogin() }
    }


    private fun attemptLogin() = async(UI) {
        if (inProgress) {
            return@async
        }
        // Reset errors.
        viewUsername!!.error = null
        viewPassword!!.error = null
        // Get values of the fields.
        val username = viewUsername!!.text.toString()
        val password = viewPassword!!.text.toString()

        var focusView: View? = null

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            viewUsername!!.error = getString(R.string.error_field_required)
            viewUsername!!.requestFocus()
            return@async
        }
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && password.length < 8) {
            viewPassword!!.error = getString(R.string.error_invalid_password)
            viewPassword!!.requestFocus()
            return@async
        }
        // Show a progress spinner, and perform the login.
        showProgress(true)
        val success = async(CommonPool) { api.attemptLogin(username, password) }.await()
        showProgress(false)
        if (success) {
            finish()
            return@async
        }
        viewPassword!!.error = getString(R.string.error_incorrect_password)
        viewPassword!!.requestFocus()
    }

    private fun showProgress(busy: Boolean) {
        inProgress = busy
        viewLoginForm!!.visibility = if (busy) View.GONE else View.VISIBLE
        viewProgress!!.visibility = if (busy) View.VISIBLE else View.GONE
    }
}
