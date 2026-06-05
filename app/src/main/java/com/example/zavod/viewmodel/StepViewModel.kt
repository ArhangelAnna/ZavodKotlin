package com.example.zavod.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zavod.model.Step
import com.example.zavod.model.StepRequest
import com.example.zavod.model.StepResult
import com.example.zavod.repository.RepositoryCallback
import com.example.zavod.repository.StepRepository

class StepViewModel(
    private val repository: StepRepository
) : ViewModel() {

    private var sessionId: String? = null
    private var step: Step? = null

    private var tagId: String? = null
    private var inputValue: String? = null
    private var photoPath: String? = null
    private var commentValue: String? = null

    private var lastRequest: StepRequest? = null

    private var steps: List<Step> = emptyList()
    private var currentStep: Step? = null

    private val _loadedStep = MutableLiveData<Step>()
    val loadedStep: LiveData<Step> = _loadedStep

    private val _checkResult = MutableLiveData<StepResult>()
    val checkResult: LiveData<StepResult> = _checkResult

    private val _photoResult = MutableLiveData<StepResult>()
    val photoResult: LiveData<StepResult> = _photoResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun init(sessionId: String?, step: Step?) {
        this.sessionId = sessionId
        this.step = step
    }

    fun setSteps(steps: List<Step>) {
        this.steps = steps
    }

    fun getSteps(): List<Step> {
        return steps
    }

    fun setCurrentStep(step: Step?) {
        this.currentStep = step
    }

    fun getCurrentStep(): Step? {
        return currentStep
    }

    fun loadStep(stepId: String) {
        repository.getStep(stepId, object : RepositoryCallback<Step> {
            override fun onSuccess(data: Step) {
                _loadedStep.value = data
            }

            override fun onError(message: String) {
                _error.value = message
            }
        })
    }

    fun getSessionId(): String? {
        return sessionId
    }

    fun setSessionId(sessionId: String?) {
        this.sessionId = sessionId
    }

    fun getStep(): Step? {
        return step
    }

    fun setStep(step: Step?) {
        this.step = step
    }

    fun setTag(tagId: String?) {
        this.tagId = tagId
    }

    fun getTag(): String? {
        return tagId
    }

    fun setInput(value: String?) {
        this.inputValue = value
    }

    fun getInput(): String? {
        return inputValue
    }

    fun setPhoto(path: String?) {
        this.photoPath = path
    }

    fun setComment(comment: String?) {
        this.commentValue = comment
    }

    fun getComment(): String? {
        return commentValue
    }

    fun buildRequest(): StepRequest {
        val current = step

        val request = StepRequest(
            sessionId = sessionId,
            stepId = current?.id,
            tagId = tagId,
            value = inputValue,
            comment = commentValue
        )

        lastRequest = request

        return request
    }

    fun getLastRequest(): StepRequest? {
        return lastRequest
    }

    fun checkStep(request: StepRequest) {
        lastRequest = request

        repository.checkStep(request, object : RepositoryCallback<StepResult> {
            override fun onSuccess(data: StepResult) {
                _checkResult.value = data
            }

            override fun onError(message: String) {
                _error.value = message
            }
        })
    }

    fun finalizeStep() {
        val request = lastRequest

        if (request == null) {
            _error.value = "Нет данных шага"
            return
        }

        checkStep(request)
    }

    fun uploadPhoto() {
        val current = step

        if (current == null) {
            _error.value = "Шаг не найден"
            return
        }

        repository.uploadPhoto(
            sessionId = sessionId.orEmpty(),
            stepId = current.id.orEmpty(),
            tagId = tagId,
            value = inputValue,
            commentValue = commentValue,
            photoPath = photoPath,
            callback = object : RepositoryCallback<StepResult> {
                override fun onSuccess(data: StepResult) {
                    _photoResult.value = data
                }

                override fun onError(message: String) {
                    _error.value = message
                }
            }
        )
    }
}