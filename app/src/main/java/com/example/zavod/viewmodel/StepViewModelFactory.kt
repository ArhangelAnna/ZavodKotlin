package com.example.zavod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zavod.repository.StepRepository

class StepViewModelFactory(
    private val repository: StepRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepViewModel::class.java)) {
            return StepViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}