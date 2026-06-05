package com.example.zavod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zavod.repository.RepairRepository

class FinishViewModelFactory(
    private val repository: RepairRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinishViewModel::class.java)) {
            return FinishViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}