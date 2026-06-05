package com.example.zavod.model

import com.google.gson.annotations.SerializedName

data class StartResponse(
    @SerializedName("sessionId")
    var sessionId: String? = null,

    @SerializedName("firstStep")
    var firstStep: Step? = null,

    @SerializedName("totalSteps")
    var totalSteps: Int = 0,

    @SerializedName("equipment")
    var equipment: Equipment? = null
)