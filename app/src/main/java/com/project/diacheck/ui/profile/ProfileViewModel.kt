package com.project.diacheck.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.diacheck.data.preference.UserModel
import com.project.diacheck.data.remote.repository.UserRepository
import com.project.diacheck.data.remote.response.UserData
import com.project.diacheck.uriToFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    private val _updateStatus = MutableStateFlow<String?>(null)
    val updateStatus: StateFlow<String?> get() = _updateStatus

    private val _userProfile = MutableStateFlow<UserData?>(null)
    val userProfile: StateFlow<UserData?> get() = _userProfile

    val user = userProfile.asLiveData()

    fun fetchUserById(userId: Int) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _userProfile.value = user
        }
    }

    fun getUserSession(): Flow<UserModel> {
        return repository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun updateUser(
        userId: Int?,
        name: String?,
        email: String?,
        password: String?,
        context: Context,
        avatarUri: Uri?
    ) {
        if (userId == null) return
        viewModelScope.launch {
            val avatarFile = avatarUri?.let { uriToFile(it, context) }
            val result = repository.updateUser(userId, name, email, password, avatarFile)
            _updateStatus.value = result?.message ?: "Gagal memperbarui data"
        }
    }

}