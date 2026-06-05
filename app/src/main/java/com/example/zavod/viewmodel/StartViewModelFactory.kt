package com.example.zavod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zavod.repository.StartRepository

class StartViewModelFactory(
    private val repository: StartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
            return StartViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}