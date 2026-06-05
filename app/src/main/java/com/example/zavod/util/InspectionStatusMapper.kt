package com.example.zavod.util

object InspectionStatusMapper {

    const val NOT_COMPLETED: String = "не проверено"
    const val IN_PROGRESS: String = "выполняется"
    const val SUCCESS: String = "проверено"
    const val FAILED: String = "проверено с замечаниями"

    const val DAY_EMPTY: Int = 0
    const val DAY_COMPLETED: Int = 1
    const val DAY_ATTENTION: Int = 2

    fun normalize(status: String?): String {
        if (status.isNullOrBlank()) {
            return NOT_COMPLETED
        }

        val value = status.trim().lowercase()

        if (
            value == "in_progress" ||
            value == "in progress" ||
            value == "running" ||
            value == "выполняется" ||
            value == "в работе"
        ) {
            return IN_PROGRESS
        }

        if (
            value == "success" ||
            value == "completed" ||
            value == "done" ||
            value == "проверено" ||
            value == "выполнено"
        ) {
            return SUCCESS
        }

        if (
            value == "failed" ||
            value == "error" ||
            value == "warning" ||
            value == "проверено с замечаниями" ||
            value == "с замечаниями"
        ) {
            return FAILED
        }

        if (
            value == "not_completed" ||
            value == "not completed" ||
            value == "not_checked" ||
            value == "not checked" ||
            value == "не проверено" ||
            value == "не выполнено"
        ) {
            return NOT_COMPLETED
        }

        return status.trim()
    }

    fun canOpen(status: String?): Boolean {
        val normalized = normalize(status)
        return normalized == NOT_COMPLETED || normalized == IN_PROGRESS
    }

    fun isCompleted(status: String?): Boolean {
        val normalized = normalize(status)
        return normalized == SUCCESS || normalized == FAILED
    }

    fun toDayStatus(status: String?): Int {
        val normalized = normalize(status)

        if (normalized == NOT_COMPLETED || normalized == IN_PROGRESS) {
            return DAY_ATTENTION
        }

        if (normalized == SUCCESS || normalized == FAILED) {
            return DAY_COMPLETED
        }

        return DAY_EMPTY
    }
}