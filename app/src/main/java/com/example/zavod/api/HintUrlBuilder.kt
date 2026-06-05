package com.example.zavod.api

import android.net.Uri

object HintUrlBuilder {

    fun build(
        equipmentId: Int,
        checkType: String?
    ): String {
        return Uri.parse(ApiConfig.BASE_URL)
            .buildUpon()
            .appendPath("hint")
            .appendQueryParameter("equipment_id", equipmentId.toString())
            .appendQueryParameter("check_type", checkType ?: "")
            .build()
            .toString()
    }

    fun build(
        equipmentId: Int,
        checkType: String?,
        stepId: String?
    ): String {
        val builder = Uri.parse(build(equipmentId, checkType))
            .buildUpon()

        if (!stepId.isNullOrBlank()) {
            builder.appendQueryParameter("step_id", stepId)
        }

        return builder.build().toString()
    }
}