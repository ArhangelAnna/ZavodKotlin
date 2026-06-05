package com.example.zavod.model.step

import com.example.zavod.model.Step

data class StepState(
    var status: Status,
    var step: Step?,
    var result: StepResult = StepResult.NONE,
    var error: String? = null,
    var inputValue: String? = null,
    var tagId: String? = null,
    var photoPath: String? = null,
    var comment: String? = null
) {
    enum class Status {
        LOADING,
        INPUT_REQUIRED,
        SCAN_REQUIRED,
        PHOTO_REQUIRED,
        READY_TO_SEND,
        SUCCESS,
        ERROR,
        FINISHED
    }

    enum class StepResult {
        NONE,
        SUCCESS,
        WARNING
    }

    companion object {
        fun loading(): StepState {
            return StepState(
                status = Status.LOADING,
                step = null
            )
        }
    }
}