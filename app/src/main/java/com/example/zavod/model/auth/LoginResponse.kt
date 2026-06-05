package com.example.zavod.model.auth

import com.example.zavod.model.Employee

data class LoginResponse(
    var success: Boolean = false,
    var token: String? = null,
    var employee: Employee? = null
)