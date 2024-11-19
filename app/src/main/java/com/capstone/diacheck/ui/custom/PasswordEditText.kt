package com.capstone.diacheck.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.capstone.diacheck.R
import com.google.android.material.textfield.TextInputEditText

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {
    init {
        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (s.toString().length < 8) {
                        setError(context.getString(R.string.password_error), null)
                    } else {
                        error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}