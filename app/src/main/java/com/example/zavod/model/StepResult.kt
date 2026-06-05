package com.example.zavod.model

data class StepResult(
    var success: Boolean = false,
    var error: String? = null,
    var finished: Boolean = false,
    var nextStep: Step? = null,
    var warning: Boolean = false
) {
    companion object {
        fun success(): StepResult {
            return StepResult(success = true)
        }

        fun error(msg: String): StepResult {
            return StepResult(
                success = false,
                error = msg
            )
        }
    }
}