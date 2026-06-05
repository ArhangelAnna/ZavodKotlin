package com.example.zavod.repository

import android.content.Context
import com.example.zavod.api.ApiClient
import com.example.zavod.api.ApiService
import com.example.zavod.model.Step
import com.example.zavod.model.StepRequest
import com.example.zavod.model.StepResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StepRepository(context: Context) {

    private val context: Context = context.applicationContext
    private val api: ApiService = ApiClient.get(this.context).create(ApiService::class.java)

    fun getStep(
        stepId: String,
        callback: RepositoryCallback<Step>
    ) {
        api.getStep(stepId).enqueue(object : Callback<Step> {
            override fun onResponse(
                call: Call<Step>,
                response: Response<Step>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onSuccess(body)
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка загрузки шага"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<Step>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    fun checkStep(
        request: StepRequest,
        callback: RepositoryCallback<StepResult>
    ) {
        api.checkStep(request).enqueue(object : Callback<StepResult> {
            override fun onResponse(
                call: Call<StepResult>,
                response: Response<StepResult>
            ) {
                val result = response.body()

                if (response.isSuccessful && result != null) {
                    if (result.success) {
                        callback.onSuccess(result)
                    } else {
                        callback.onError(ServerErrorParser.translate(result.error))
                    }
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка проверки шага"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<StepResult>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    fun uploadPhoto(
        sessionId: String,
        stepId: String,
        tagId: String?,
        value: String?,
        commentValue: String?,
        photoPath: String?,
        callback: RepositoryCallback<StepResult>
    ) {
        if (photoPath == null) {
            callback.onError("Фото не выбрано")
            return
        }

        val file = File(photoPath)

        if (!file.exists()) {
            callback.onError("Файл фото не найден")
            return
        }

        val textType = "text/plain".toMediaTypeOrNull()

        val session = sessionId.toRequestBody(textType)
        val step = stepId.toRequestBody(textType)
        val tag = (tagId ?: "").toRequestBody(textType)
        val input = (value ?: "").toRequestBody(textType)
        val comment = (commentValue ?: "").toRequestBody(textType)

        val fileBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        val photoPart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            fileBody
        )

        api.uploadStep(
            session,
            step,
            tag,
            input,
            comment,
            photoPart
        ).enqueue(object : Callback<StepResult> {
            override fun onResponse(
                call: Call<StepResult>,
                response: Response<StepResult>
            ) {
                val result = response.body()

                if (response.isSuccessful && result != null) {
                    if (result.success) {
                        callback.onSuccess(result)
                    } else {
                        callback.onError(ServerErrorParser.translate(result.error))
                    }
                } else {
                    callback.onError(
                        ServerErrorParser.fromResponse(
                            response,
                            "Ошибка загрузки фото"
                        )
                    )
                }
            }

            override fun onFailure(
                call: Call<StepResult>,
                t: Throwable
            ) {
                callback.onError(ServerErrorParser.fromThrowable(t))
            }
        })
    }

    fun cancelCheck(sessionId: String?) {
        if (sessionId.isNullOrBlank()) {
            return
        }

        val inspectionId = sessionId.toIntOrNull() ?: return

        api.cancelCheck(getAuthHeader(), inspectionId)
            .enqueue(object : Callback<okhttp3.ResponseBody> {
                override fun onResponse(
                    call: Call<okhttp3.ResponseBody>,
                    response: Response<okhttp3.ResponseBody>
                ) {
                    // результат отмены не блокирует интерфейс
                }

                override fun onFailure(
                    call: Call<okhttp3.ResponseBody>,
                    t: Throwable
                ) {
                    // если приложение закрывается без сети, redis-ключ активности всё равно истечёт по ttl
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