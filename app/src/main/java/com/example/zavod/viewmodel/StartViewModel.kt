package com.example.zavod.viewmodel

import androidx.lifecycle.ViewModel
import com.example.zavod.domain.StartSession
import com.example.zavod.repository.StartRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StartViewModel(
    private val repo: StartRepository
) : ViewModel() {

    interface Callback {
        fun onSuccess(session: StartSession)
        fun onError(error: String?)
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun start(
        tagId: String,
        typeCheck: String,
        equipmentId: Int,
        templateId: Int?,
        scheduleId: Int?,
        callback: Callback
    ) {
        executor.execute {
            try {
                val result = repo.start(
                    tagId = tagId,
                    typeCheck = typeCheck,
                    equipmentId = equipmentId,
                    templateId = templateId,
                    scheduleId = scheduleId
                )

                callback.onSuccess(result)
            } catch (e: Exception) {
                callback.onError(e.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdownNow()
    }
}