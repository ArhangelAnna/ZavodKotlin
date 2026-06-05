package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.model.Equipment
import com.example.zavod.model.EquipmentResponse
import com.example.zavod.util.InspectionStatusMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentRepository(context: Context) {

    private val api: ApiService =
        ApiClient.get(context.applicationContext).create(ApiService::class.java)

    fun getEquipment(
        passId: String,
        callback: RepositoryCallback<EquipmentResponse>
    ) {
        api.getEquipment(passId).enqueue(object : Callback<EquipmentResponse> {
            override fun onResponse(
                call: Call<EquipmentResponse>,
                response: Response<EquipmentResponse>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка загрузки оборудования"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<EquipmentResponse>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    fun getFirstNotCompletedDaily(
        passId: String,
        callback: RepositoryCallback<Equipment?>
    ) {
        getEquipment(passId, object : RepositoryCallback<EquipmentResponse> {
            override fun onSuccess(data: EquipmentResponse) {
                val target = data.equipment
                    ?.firstOrNull { equipment ->
                        isDailyInspectionNotCompleted(equipment)
                    }

                callback.onSuccess(target)
            }

            override fun onError(message: String) {
                callback.onError(message)
            }
        })
    }

    private fun isDailyInspectionNotCompleted(equipment: Equipment?): Boolean {
        if (equipment == null) {
            return false
        }

        return InspectionStatusMapper.canOpen(equipment.dailyInspectionStatus)
    }
}