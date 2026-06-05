package com.example.zavod.viewmodel

import com.example.zavod.model.Step
import com.example.zavod.model.step.StepState

class StepEngine(
    private var currentStep: Step
) {

    fun getStep(): Step {
        return currentStep
    }

    fun onInput(value: String?): StepState {
        if (currentStep.requiresInput && value.isNullOrBlank()) {
            return error("Введите значение")
        }

        if (hasExpectedTag()) {
            return StepState(
                status = StepState.Status.SCAN_REQUIRED,
                step = currentStep
            )
        }

        if (currentStep.requiresPhoto) {
            return StepState(
                status = StepState.Status.PHOTO_REQUIRED,
                step = currentStep
            )
        }

        return StepState(
            status = StepState.Status.READY_TO_SEND,
            step = currentStep
        )
    }

    fun onScan(tagId: String?): StepState {
        if (hasExpectedTag() && currentStep.expectedTag != tagId) {
            return error("Неверная NFC метка")
        }

        if (currentStep.requiresPhoto) {
            return StepState(
                status = StepState.Status.PHOTO_REQUIRED,
                step = currentStep
            )
        }

        return StepState(
            status = StepState.Status.READY_TO_SEND,
            step = currentStep
        )
    }

    fun onPhoto(): StepState {
        return StepState(
            status = StepState.Status.READY_TO_SEND,
            step = currentStep
        )
    }

    fun success(): StepState {
        return StepState(
            status = StepState.Status.SUCCESS,
            step = currentStep
        )
    }

    fun error(msg: String): StepState {
        return StepState(
            status = StepState.Status.ERROR,
            step = currentStep,
            error = msg
        )
    }

    private fun hasExpectedTag(): Boolean {
        return !currentStep.expectedTag.isNullOrBlank()
    }
}