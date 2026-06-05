package com.example.zavod.model

data class StepRequest(
    var sessionId: String?,
    var stepId: String?,
    var tagId: String?,
    var value: String?,
    var comment: String?
)