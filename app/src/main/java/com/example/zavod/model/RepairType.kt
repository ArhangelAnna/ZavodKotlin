package com.example.zavod.model

data class RepairType(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null
) {
    override fun toString(): String {
        return name.orEmpty()
    }
}