package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.domain.StartMapper
import com.example.zavod.domain.StartSession
import com.example.zavod.model.StartResponse
import retrofit2.Response
import java.io.IOException

class StartRepository(context: Context) {

    private val context: Context = context.applicationContext
    private val api: ApiService = ApiClient.get(this.context).create(ApiService::class.java)

    @Throws(Exception::class)
    fun start(
        tagId: String,
        typeCheck: String,
        equipmentId: Int,
        templateId: Int?,
        scheduleId: Int?
    ): StartSession {
        return try {
            val response: Response<StartResponse> = api.startCheck(
                getAuthHeader(),
                tagId,
                typeCheck,
                equipmentId,
                templateId,
                scheduleId
            ).execute()

            if (response.isSuccessful && response.body() != null) {
                StartMapper.map(response.body()!!)
            } else {
                throw Exception(
                    ServerErrorParser.fromResponse(
                        response,
                        "Не удалось начать проверку"
                    )
                )
            }
        } catch (e: IOException) {
            throw Exception(ServerErrorParser.fromThrowable(e))
        }
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