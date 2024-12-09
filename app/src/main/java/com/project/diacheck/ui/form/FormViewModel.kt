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
import com.project.diacheck.data.remote.request.CreateHistoryRequest
import com.project.diacheck.data.remote.response.AddHistoryResponse
import com.project.diacheck.data.remote.response.DetailHistoriesResponse
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.data.remote.response.PredictionResponse
import com.project.diacheck.data.remote.response.SubmitFormItem
import com.project.diacheck.data.remote.retrofit.ApiML
import kotlinx.coroutines.launch

class FormViewModel(
    private val formRepository: FormRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _formResult = MutableLiveData<Result<List<ListFormItem>>>()
    val formResult: LiveData<Result<List<ListFormItem>>> get() = _formResult
    private val _createHistoryResult = MutableLiveData<Result<AddHistoryResponse>>()
    val createHistoryResult: LiveData<Result<AddHistoryResponse>> get() = _createHistoryResult

    fun findFormByUserId(userId: Int) {
        viewModelScope.launch {
            _formResult.postValue(Result.Loading)
            try {
                val result = formRepository.getFormById(userId)
                _formResult.postValue(Result.Success(result))
            } catch (e: Exception) {
                _formResult.postValue(Result.Error(e.message ?: "An error occurred"))
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun createHistory(request: CreateHistoryRequest) {
        viewModelScope.launch {
            _createHistoryResult.value = Result.Loading
            try {
                val response = formRepository.createHistory(request)
                _createHistoryResult.value = Result.Success(response)
            } catch (e: Exception) {
                _createHistoryResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }
}