package com.capstone.diacheck.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.capstone.diacheck.R
import com.google.android.material.textfield.TextInputEditText

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                s?.let {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                        error = null
                    } else {
                        setError(context.getString(R.string.email_error), null)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }
}