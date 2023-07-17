package com.example.mystory.auth

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.CustomPasswordView
import com.example.mystory.R
import com.example.mystory.Result
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)
        etName = rootView.findViewById(R.id.ed_register_name)
        etEmail = rootView.findViewById(R.id.ed_register_email)
        etPassword = rootView.findViewById(R.id.et_register_password)
        btnRegister = rootView.findViewById(R.id.btnRegister)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        etPassword.addTextChangedListener(CustomPasswordView(etPassword))



        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val role = "Admin"
            val avatar = "https://static.vecteezy.com/system/resources/previews/002/275/847/non_2x/male-avatar-profile-icon-of-smiling-caucasian-man-vector.jpg"

            if (isValidEmail(email)) {
                viewModel.register(name, email, password)
            } else {
                val snackbar = Snackbar.make(view, "Registration failed: Invalid email address!", Snackbar.LENGTH_LONG)
                val snackbarView = snackbar.view
                val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.red))
                snackbar.show()
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val registerResponse = result.data
                    val message = registerResponse?.message

                    val snackbar = Snackbar.make(view, "Hey, $message!", Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view
                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.green))
                    snackbar.show()


                }
                is Result.Error -> {
                    val exception = result.exception
                    // Handle registration error

                    val snackbar = Snackbar.make(view, "Registration failed: ${exception.message}", Snackbar.LENGTH_LONG)
                    val snackbarView = snackbar.view
                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)
                    snackbar.setBackgroundTint(resources.getColor(com.example.mystory.R.color.red))
                    snackbar.show()
                }
                else -> {
                    // Handle registration error
                    val snackbar = Snackbar.make(view, "Registration failed: Contact admin!", Snackbar.LENGTH_LONG)
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
