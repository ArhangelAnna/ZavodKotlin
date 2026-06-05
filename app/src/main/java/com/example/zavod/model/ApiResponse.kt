package com.example.zavod.model

data class ApiResponse(
    var success: Boolean = false,
    var status: String? = null,
    var error: String? = null,
    var message: String? = null
)