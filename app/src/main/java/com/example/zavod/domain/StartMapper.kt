package com.example.zavod.domain

import com.example.zavod.model.StartResponse

object StartMapper {

    fun map(response: StartResponse): StartSession {
        return StartSession(
            sessionId = response.sessionId,
            firstStep = response.firstStep,
            totalSteps = response.totalSteps,
            equipment = response.equipment
        )
    }
}