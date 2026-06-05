package com.example.zavod.model

import com.google.gson.annotations.SerializedName

data class Schedule(
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("date")
    var date: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("task_name")
    var taskName: String? = null,

    @SerializedName("equipment_id")
    var equipmentId: Int = 0,

    @SerializedName("equipment_name")
    var equipmentName: String? = null,

    @SerializedName("check_type")
    var checkType: String? = null,

    @SerializedName("template_id")
    var templateId: Int = 0
)