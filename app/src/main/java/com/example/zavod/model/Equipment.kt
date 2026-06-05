package com.example.zavod.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Equipment(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("location")
    var location: String? = null,

    @SerializedName("daily_inspection_status")
    var dailyInspectionStatus: String? = null,

    @SerializedName("inspection_templates")
    var templates: List<InspectionTemplate>? = null
) : Serializable