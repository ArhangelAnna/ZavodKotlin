package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.model.ScheduleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleRepository(context: Context) {

    private val api: ApiService =
        ApiClient.get(context.applicationContext).create(ApiService::class.java)

    fun getSchedule(
        passId: String,
        callback: RepositoryCallback<ScheduleResponse>
    ) {
        api.getSchedule(passId).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(
                call: Call<ScheduleResponse>,
                response: Response<ScheduleResponse>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка загрузки расписания"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<ScheduleResponse>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }
}