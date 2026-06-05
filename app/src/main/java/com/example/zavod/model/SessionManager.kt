package com.example.zavod.model

class SessionManager private constructor() {

    var sessionId: String? = null
    var currentStep: Step? = null
    var stepIndex: Int = 0
    var totalSteps: Int = 0

    var equipmentId: Int = 0
    var equipmentName: String? = null

    fun clear() {
        sessionId = null
        currentStep = null
        stepIndex = 0
        totalSteps = 0

        equipmentId = 0
        equipmentName = null
    }

    companion object {
        private var instance: SessionManager? = null

        fun get(): SessionManager {
            if (instance == null) {
                instance = SessionManager()
            }
            return instance!!
        }
    }
}