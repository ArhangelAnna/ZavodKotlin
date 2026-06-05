package com.example.zavod.domain

import com.example.zavod.model.Equipment
import com.example.zavod.model.Step

data class StartSession(
    var sessionId: String? = null,
    var firstStep: Step? = null,
    var totalSteps: Int = 0,
    var equipment: Equipment? = null
)