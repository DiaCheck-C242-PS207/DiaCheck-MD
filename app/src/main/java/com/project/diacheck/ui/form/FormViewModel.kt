package com.project.diacheck.ui.form

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.preference.UserModel
import com.project.diacheck.data.remote.repository.FormRepository
import com.project.diacheck.data.remote.repository.UserRepository
import com.project.diacheck.data.remote.response.DetailHistoriesResponse
import com.project.diacheck.data.remote.response.SubmitFormItem
import kotlinx.coroutines.launch

class FormViewModel(
    private val formRepository: FormRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val historyDetail = MutableLiveData<Result<DetailHistoriesResponse>>()

    fun getHistoryById(id: Int) {
        viewModelScope.launch {
            val result = formRepository.getHistoryById(id)
            historyDetail.value = result
        }
    }

    fun findFormByUserId(userId: String): LiveData<Result<List<HistoryEntity>>> {
        return formRepository.getFormById(userId)
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun submitForm(formItem: SubmitFormItem) {
        viewModelScope.launch {
            try {
                formRepository.submitForm(formItem)
            } catch (e: Exception) {
                Log.e("FormViewModel", "Error: ${e.message}")
            }
        }
    }
}