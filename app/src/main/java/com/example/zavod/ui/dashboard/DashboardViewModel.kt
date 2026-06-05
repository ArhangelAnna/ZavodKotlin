package com.example.zavod.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zavod.model.DayItem
import com.example.zavod.model.Schedule
import com.example.zavod.model.ScheduleResponse
import com.example.zavod.repository.RepositoryCallback
import com.example.zavod.repository.ScheduleRepository
import com.example.zavod.util.InspectionStatusMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class DashboardViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _daysList = MutableLiveData<List<DayItem>>()
    val daysList: LiveData<List<DayItem>> = _daysList

    private val _dayInfoText = MutableLiveData<String>()
    val dayInfoText: LiveData<String> = _dayInfoText

    private val _selectedDaySchedules = MutableLiveData<List<Schedule>>()
    val selectedDaySchedules: LiveData<List<Schedule>> = _selectedDaySchedules

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var allSchedules: List<Schedule> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSchedule(passId: String?) {
        if (passId.isNullOrBlank()) {
            _error.value = "Нет авторизации"
            return
        }

        repository.getSchedule(passId, object : RepositoryCallback<ScheduleResponse> {
            override fun onSuccess(data: ScheduleResponse) {
                initData(data.schedule)
            }

            override fun onError(message: String) {
                _error.value = message
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initData(schedules: List<Schedule>?) {
        allSchedules = schedules ?: emptyList()
        generateDays()

        val todayPos = findTodayPosition()
        if (todayPos != -1) {
            selectDay(todayPos)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDays() {
        val today = LocalDate.now()
        val startDate = today.minusWeeks(2)
        val endDate = today.plusWeeks(4)

        val list = mutableListOf<DayItem>()
        var current = startDate

        while (!current.isAfter(endDate)) {
            val day = DayItem(current, current == today)

            var status = InspectionStatusMapper.DAY_EMPTY

            for (schedule in allSchedules) {
                val scheduleDate = schedule.date ?: continue

                if (LocalDate.parse(scheduleDate) == current) {
                    val scheduleStatus = InspectionStatusMapper.toDayStatus(schedule.status)

                    if (scheduleStatus == InspectionStatusMapper.DAY_ATTENTION) {
                        status = InspectionStatusMapper.DAY_ATTENTION
                        break
                    }

                    if (scheduleStatus == InspectionStatusMapper.DAY_COMPLETED) {
                        status = InspectionStatusMapper.DAY_COMPLETED
                    }
                }
            }

            day.status = status
            list.add(day)

            current = current.plusDays(1)
        }

        _daysList.value = list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun selectDay(position: Int) {
        val currentList = _daysList.value?.toMutableList() ?: return

        if (position < 0 || position >= currentList.size) {
            return
        }

        for (day in currentList) {
            day.isSelected = false
        }

        val selectedDay = currentList[position]
        selectedDay.isSelected = true

        _daysList.value = currentList

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

        var dayOfWeek = selectedDay.date.dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale("ru"))

        dayOfWeek = dayOfWeek.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString()
        }

        _dayInfoText.value = selectedDay.date.format(formatter) + " - " + dayOfWeek

        val selectedDateStr = selectedDay.date.toString()

        val filtered = allSchedules.filter { schedule ->
            schedule.date == selectedDateStr
        }

        _selectedDaySchedules.value = filtered
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun findTodayPosition(): Int {
        val list = _daysList.value ?: return -1
        val today = LocalDate.now()

        for (i in list.indices) {
            if (list[i].date == today) {
                return i
            }
        }

        return -1
    }
}