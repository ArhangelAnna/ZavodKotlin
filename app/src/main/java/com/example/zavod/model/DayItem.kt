package com.example.zavod.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DayItem(
    var date: LocalDate,
    var isSelected: Boolean
) {
    // статус: 0 - нет задач, 1 - проверено, 2 - есть невыполненные
    var status: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfWeek(): String {
        return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayOfMonth(): Int {
        return date.dayOfMonth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonth(): String {
        return date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
}