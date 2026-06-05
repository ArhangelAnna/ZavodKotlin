package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.model.RepairRequest
import com.example.zavod.model.RepairTypesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepairRepository(context: Context) {

    private val context: Context = context.applicationContext
    private val api: ApiService = ApiClient.get(this.context).create(ApiService::class.java)

    fun getRepairTypes(
        callback: RepositoryCallback<RepairTypesResponse>
    ) {
        api.getRepairTypes().enqueue(object : Callback<RepairTypesResponse> {
            override fun onResponse(
                call: Call<RepairTypesResponse>,
                response: Response<RepairTypesResponse>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Не удалось загрузить типы ремонтов"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<RepairTypesResponse>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    fun createRepair(
        request: RepairRequest,
        callback: RepositoryCallback<Void?>
    ) {
        api.createRepair(getAuthHeader(), request).enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    callback.onSuccess(null)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка отправки заявки"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<Void>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    private fun getAuthHeader(): String {
        val token = context
            .getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("token", null)

        if (token.isNullOrBlank()) {
            return ""
        }

        return "Bearer $token"
    }
}