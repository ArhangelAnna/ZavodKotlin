package com.example.zavod.model

data class ProfileResponse(
    var employee: Employee? = null,
    var equipment: List<Equipment>? = null
)