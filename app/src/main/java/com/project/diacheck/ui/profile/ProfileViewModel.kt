package com.project.diacheck.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.diacheck.data.preference.UserModel
import com.project.diacheck.data.remote.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

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
        userId: Unit,
        name: String,
        imageFile: File
    ) = repository.updateUser(userId, name, imageFile)

}