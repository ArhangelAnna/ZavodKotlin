package com.example.zavod.repository

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ServerErrorParser {

    fun fromResponse(
        response: Response<*>?,
        fallback: String
    ): String {
        if (response == null) {
            return fallback
        }

        val serverMessage = readErrorBody(response.errorBody())

        if (serverMessage.isNullOrBlank()) {
            return "$fallback (код ${response.code()})"
        }

        return translate(serverMessage)
    }

    fun fromThrowable(throwable: Throwable?): String {
        return when (throwable) {
            is SocketTimeoutException -> "Сервер не отвечает. Попробуйте позже"
            is UnknownHostException -> "Сервер не найден. Проверьте адрес сервера"
            is ConnectException -> "Нет соединения с сервером"
            else -> {
                val message = throwable?.message

                if (message.isNullOrBlank()) {
                    "Сервер недоступен"
                } else {
                    message
                }
            }
        }
    }

    fun translate(message: String?): String {
        if (message.isNullOrBlank()) {
            return "Ошибка сервера"
        }

        val value = message.trim()
        val lower = value.lowercase()

        return when {
            lower.contains("template not found") ->
                "Шаблон проверки не найден. Проверьте стартовую NFC-метку"

            lower.contains("equipment not found") ->
                "Оборудование не найдено"

            lower.contains("check type not found") ->
                "Тип проверки не найден"

            lower.contains("hint image not found") ->
                "Изображение подсказки не найдено"

            lower.contains("step hint not found") ->
                "Подсказка для шага не найдена"

            lower.contains("session not found") ->
                "Сессия проверки не найдена"

            lower.contains("wrong step") ->
                "Неверный шаг проверки"

            lower.contains("tagid required") ->
                "Сканируйте NFC-метку"

            lower.contains("wrong nfc") ->
                "Неверная NFC-метка"

            lower.contains("value required") ->
                "Введите значение"

            lower.contains("invalid pass_id") ->
                "Неверный табельный номер"

            else -> value
        }
    }

    private fun readErrorBody(errorBody: ResponseBody?): String? {
        if (errorBody == null) {
            return null
        }

        return try {
            val raw = errorBody.string()

            if (raw.isBlank()) {
                null
            } else {
                extractMessage(raw)
            }
        } catch (_: IOException) {
            null
        }
    }

    private fun extractMessage(raw: String): String {
        return try {
            val obj = JSONObject(raw)

            if (obj.has("detail")) {
                val detail = obj.get("detail")

                if (detail is String) {
                    return detail
                }

                if (detail is JSONArray && detail.length() > 0) {
                    val first = detail.optJSONObject(0)

                    if (first != null && first.has("msg")) {
                        return first.optString("msg")
                    }
                }
            }

            when {
                obj.has("error") -> obj.optString("error")
                obj.has("message") -> obj.optString("message")
                obj.has("status") -> obj.optString("status")
                else -> raw
            }
        } catch (_: Exception) {
            raw
        }
    }
}