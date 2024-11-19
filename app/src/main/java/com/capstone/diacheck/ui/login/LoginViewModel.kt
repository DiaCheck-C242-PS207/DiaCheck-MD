package com.capstone.diacheck.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.diacheck.data.remote.repository.UserRepository
import com.capstone.diacheck.data.preference.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
    fun login(email: String, password: String) = repository.login(email, password)
}