package com.example.zavod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zavod.model.RepairRequest
import com.example.zavod.model.RepairTypesResponse
import com.example.zavod.repository.RepairRepository
import com.example.zavod.repository.RepositoryCallback

class FinishViewModel(
    private val repository: RepairRepository
) : ViewModel() {

    private val _repairTypes = MutableLiveData<RepairTypesResponse>()
    val repairTypes: LiveData<RepairTypesResponse> = _repairTypes

    private val _repairSent = MutableLiveData<Boolean>()
    val repairSent: LiveData<Boolean> = _repairSent

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadRepairTypes() {
        repository.getRepairTypes(object : RepositoryCallback<RepairTypesResponse> {
            override fun onSuccess(data: RepairTypesResponse) {
                _repairTypes.value = data
            }

            override fun onError(message: String) {
                _error.value = message
            }
        })
    }

    fun createRepair(request: RepairRequest) {
        repository.createRepair(request, object : RepositoryCallback<Void?> {
            override fun onSuccess(data: Void?) {
                _repairSent.value = true
            }

            override fun onError(message: String) {
                _error.value = message
            }
        })
    }
}