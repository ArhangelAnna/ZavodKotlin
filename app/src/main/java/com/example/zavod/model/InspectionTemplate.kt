package com.example.zavod.model

import com.google.gson.annotations.SerializedName

data class InspectionTemplate(
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("equipment_type_id")
    var equipmentTypeId: Int = 0,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("check_type")
    var checkType: String? = null,

    @SerializedName("frequency")
    var frequency: String? = null,

    @SerializedName("steps")
    var steps: List<TemplateStep>? = null
) {
    data class TemplateStep(
        @SerializedName("id")
        var id: Int = 0,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("description")
        var description: String? = null,

        @SerializedName("step_order")
        var stepOrder: Int = 0,

        @SerializedName("data_type")
        var dataType: String? = null,

        @SerializedName("min_value")
        var minValue: Float? = null,

        @SerializedName("max_value")
        var maxValue: Float? = null
    )
}