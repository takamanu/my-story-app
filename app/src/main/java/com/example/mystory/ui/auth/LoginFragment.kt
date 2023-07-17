package com.example.mystory.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.*
import com.example.mystory.utils.helper.Constant
import com.example.mystory.ui.stories.MainActivity
import com.example.mystory.utils.CustomPasswordView
import com.example.mystory.utils.PreferencesHelper
import com.example.mystory.utils.Result
import com.example.mystory.vm.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class LoginFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionPreferences: SharedPreferences
    lateinit var sharedPref: PreferencesHelper
    private lateinit var passwordView: CustomPasswordView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        etEmail = rootView.findViewById(R.id.ed_login_email)
        etPassword = rootView.findViewById(R.id.ed_passwordBox)
//        passwordView = rootView.findViewById(R.id.ed_passwordBox)
        btnLogin = rootView.findViewById(R.id.btn_login)
        sessionPreferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = PreferencesHelper(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        etPassword.addTextChangedListener(CustomPasswordView(etPassword))

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (isValidEmail(email)) {
                viewModel.login(email, password)
            } else {
                val snackbar = Snackbar.make(view, "Invalid email address!", Snackbar.LENGTH_LONG)
                val snackbarView = snackbar.view
                val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.red))
                snackbar.show()

            }


        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val loginResponse = result.data
                    val token = loginResponse?.loginResult?.token
                    val user = loginResponse?.loginResult

                    if (token != null) {
                        sharedPref.put(Constant.PREF_TOKEN, token)
                    }

                    Log.d("Login", "Login response: $loginResponse")
                    Log.d("Login", "Token: $token")
                    Log.d("Login", "User: $user")
                    val username = user?.name

                    // Save login response JSON to session
                    val gson = Gson()
                    val loginResponseJson = gson.toJson(loginResponse)
                    sessionPreferences.edit().putString("loginResponse", loginResponseJson).apply()
                    Log.d("Login", "Kalo disimpen jadi string: $loginResponseJson")

                    val snackbar = Snackbar.make(view, "Login success!", Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view
                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.green))
                    snackbar.show()

                    // Handle successful login response
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra("username", username)
                    intent.putExtra("token", token)
//                    intent.putExtra("avatar", avatar)
                    startActivity(intent)
                    activity?.finish()
                }
                is Result.Error -> {
                    val exception = result.exception
                    // Handle login error
                    val snackbar = Snackbar.make(view, "Login failed: Password error or no connection!", Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view
                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.red))
                    snackbar.show()
                }
                else -> {
                    // Handle login error
                    val snackbar = Snackbar.make(view, "Login failed: Contact Admin!", Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view
                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.red))
                    snackbar.show()
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

