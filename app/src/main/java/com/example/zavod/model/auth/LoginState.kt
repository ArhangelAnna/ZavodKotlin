package com.example.zavod.model.auth

data class LoginState(
    var loading: Boolean = false,
    var success: Boolean = false,
    var error: String? = null
)