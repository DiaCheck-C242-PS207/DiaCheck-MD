package com.project.diacheck.ui.signup

import androidx.lifecycle.ViewModel
import com.project.diacheck.data.remote.repository.UserRepository

class SignupViewModel (private val repository: UserRepository) : ViewModel() {
    fun signup(name: String, email: String, password: String) = repository.signup(name, email, password)
}