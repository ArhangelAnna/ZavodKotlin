package com.example.zavod.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Step(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("expectedTag")
    var expectedTag: String? = null,

    @SerializedName("type")
    var type: String? = null,

    @SerializedName("requiresInput")
    var requiresInput: Boolean = false,

    @SerializedName("minValue")
    var minValue: Int? = null,

    @SerializedName("maxValue")
    var maxValue: Int? = null,

    @SerializedName("requiresPhoto")
    var requiresPhoto: Boolean = false
) : Serializable