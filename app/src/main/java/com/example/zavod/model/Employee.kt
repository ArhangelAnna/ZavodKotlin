package com.example.zavod.model

import com.google.gson.annotations.SerializedName

data class Employee(
    var id: Int = 0,

    @SerializedName("full_name")
    var fullName: String? = null,

    var position: String? = null,

    @SerializedName("pass_id")
    var passId: String? = null,

    var schedules: List<Schedule>? = null
)