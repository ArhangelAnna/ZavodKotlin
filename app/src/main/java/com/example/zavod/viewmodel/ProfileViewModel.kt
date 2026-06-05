package com.example.zavod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zavod.model.ProfileResponse
import com.example.zavod.repository.ProfileRepository
import com.example.zavod.repository.RepositoryCallback

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadProfile(passId: String?) {
        if (passId.isNullOrBlank()) {
            _error.value = "Нет авторизации"
            return
        }

        repository.getProfile(passId, object : RepositoryCallback<ProfileResponse> {
            override fun onSuccess(data: ProfileResponse) {
                _profile.value = data
            }

            override fun onError(message: String) {
                _error.value = message
            }
        })
    }
}