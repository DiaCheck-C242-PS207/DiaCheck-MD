package com.capstone.diacheck.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.diacheck.data.remote.repository.FormRepository
import com.capstone.diacheck.data.remote.repository.UserRepository
import com.capstone.diacheck.di.Injection
import com.capstone.diacheck.ui.form.FormViewModel
import com.capstone.diacheck.ui.login.LoginViewModel
import com.capstone.diacheck.ui.main.MainViewModel
import com.capstone.diacheck.ui.profile.ProfileViewModel
import com.capstone.diacheck.ui.signup.SignupViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val formRepository: FormRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(FormViewModel::class.java) -> {
                FormViewModel(formRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideUserRepository(context),
                        Injection.provideFormRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}