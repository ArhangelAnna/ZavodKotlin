package com.example.zavod.model.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("pass_id")
    var passId: String
)