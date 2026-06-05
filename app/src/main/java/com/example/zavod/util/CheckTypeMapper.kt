package com.example.zavod.util

object CheckTypeMapper {

    const val DAILY: String = "daily"
    const val SCHEDULED: String = "scheduled"

    fun normalizeForStart(checkType: String?): String {
        val value = normalizeBase(checkType)
        return value ?: DAILY
    }

    fun normalizeForSchedule(checkType: String?): String {
        normalizeBase(checkType)

        // если элемент открыт из расписания, он должен запускаться как scheduled
        return SCHEDULED
    }

    private fun normalizeBase(checkType: String?): String? {
        if (checkType.isNullOrBlank()) {
            return null
        }

        val value = checkType.trim().lowercase()

        if (
            value == "scheduled" ||
            value == "schedule" ||
            value == "not_daily" ||
            value == "not daily" ||
            value == "плановая" ||
            value == "по расписанию"
        ) {
            return SCHEDULED
        }

        if (
            value == "daily" ||
            value == "ежедневная" ||
            value == "ежедневно"
        ) {
            return DAILY
        }

        return value
    }
}