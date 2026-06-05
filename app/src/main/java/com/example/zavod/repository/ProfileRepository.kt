package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.model.ProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository(context: Context) {

    private val api: ApiService =
        ApiClient.get(context.applicationContext).create(ApiService::class.java)

    fun getProfile(
        passId: String,
        callback: RepositoryCallback<ProfileResponse>
    ) {
        api.getProfile(passId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка загрузки профиля"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<ProfileResponse>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }
}