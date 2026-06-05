package com.example.zavod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zavod.model.Equipment
import com.example.zavod.repository.EquipmentRepository
import com.example.zavod.repository.RepositoryCallback

class MainViewModel(
    private val repository: EquipmentRepository
) : ViewModel() {

    private val _dailyEquipment = MutableLiveData<Equipment?>()
    val dailyEquipment: LiveData<Equipment?> = _dailyEquipment

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun loadDailyEquipment(passId: String?) {
        if (passId.isNullOrBlank()) {
            _message.value = "Нет авторизации"
            return
        }

        repository.getFirstNotCompletedDaily(passId, object : RepositoryCallback<Equipment?> {
            override fun onSuccess(data: Equipment?) {
                if (data == null) {
                    _message.value = "Все ежедневные проверки выполнены"
                } else {
                    _dailyEquipment.value = data
                }
            }

            override fun onError(message: String) {
                _message.value = message
            }
        })
    }
}