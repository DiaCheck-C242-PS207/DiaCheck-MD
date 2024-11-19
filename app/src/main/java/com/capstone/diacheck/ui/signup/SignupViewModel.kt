package com.capstone.diacheck.ui.signup

import androidx.lifecycle.ViewModel
import com.capstone.diacheck.data.remote.repository.UserRepository

class SignupViewModel (private val repository: UserRepository) : ViewModel() {
    fun signup(name: String, email: String, password: String) = repository.signup(name, email, password)
}