package com.example.zavod.model

import com.google.gson.annotations.SerializedName

data class RepairRequest(
    @SerializedName("defect")
    var defect: String,

    @SerializedName("category")
    var category: String,

    @SerializedName("equipmentId")
    var equipmentId: Int,

    @SerializedName("priority")
    var priority: String
)