package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.model.auth.LoginRequest
import com.example.zavod.model.auth.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(context: Context) {

    private val context: Context = context.applicationContext
    private val api: ApiService = ApiClient.get(this.context).create(ApiService::class.java)

    fun login(
        passId: String,
        callback: RepositoryCallback<LoginResponse>
    ) {
        api.login(LoginRequest(passId)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка авторизации"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<LoginResponse>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    fun saveAuth(body: LoginResponse) {
        val employee = body.employee

        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .edit()
            .putString("token", body.token)
            .putString("pass_id", employee?.passId)
            .putInt("employee_id", employee?.id ?: 0)
            .putString("employee_name", employee?.fullName)
            .apply()
    }
}