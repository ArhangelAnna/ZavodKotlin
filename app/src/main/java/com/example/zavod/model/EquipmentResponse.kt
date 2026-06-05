package com.example.zavod.model

import com.google.gson.annotations.SerializedName

data class EquipmentResponse(
    @SerializedName("equipment")
    var equipment: List<Equipment>? = null
)