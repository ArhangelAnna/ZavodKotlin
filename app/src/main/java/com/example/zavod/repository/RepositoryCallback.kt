package com.example.zavod.repository

interface RepositoryCallback<T> {
    fun onSuccess(data: T)
    fun onError(message: String)
}