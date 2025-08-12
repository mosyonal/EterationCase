package com.emreonal.eterationcase.core

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(
        val message: String? = "An Error Occurred",
        val throwable: Throwable? = null
    ) :
        Result<Nothing>()

    object Loading : Result<Nothing>()
}
