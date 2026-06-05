package com.example.zavod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zavod.model.auth.LoginResponse
import com.example.zavod.model.auth.LoginState
import com.example.zavod.repository.AuthRepository
import com.example.zavod.repository.RepositoryCallback

class LoginViewModel(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableLiveData<LoginState>()
    val state: LiveData<LoginState> = _state

    fun login(passId: String) {
        _state.value = LoginState(
            loading = true,
            success = false,
            error = null
        )

        repo.login(passId, object : RepositoryCallback<LoginResponse> {
            override fun onSuccess(data: LoginResponse) {
                if (!data.success) {
                    _state.value = LoginState(
                        loading = false,
                        success = false,
                        error = "Неверный пропуск"
                    )
                    return
                }

                repo.saveAuth(data)

                _state.value = LoginState(
                    loading = false,
                    success = true,
                    error = null
                )
            }

            override fun onError(message: String) {
                _state.value = LoginState(
                    loading = false,
                    success = false,
                    error = message
                )
            }
        })
    }
}