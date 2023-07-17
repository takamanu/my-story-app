package com.example.mystory

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText

class CustomPasswordView(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.toString().length < 8) {
            editText.setError("Password must be at least 8 characters long", null)
        } else {
            editText.error = null
        }
    }

    override fun afterTextChanged(s: Editable?) {}
}

