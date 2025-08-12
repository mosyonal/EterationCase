package com.emreonal.eterationcase.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ApiCall<T>(
    private val viewModel: ViewModel,
    private val call: () -> Flow<Result<T>>
) {
    private var onSuccess: (T) -> Unit = {}
    private var onError: (String?, Throwable?) -> Unit = { _, _ -> }
    private var onLoading: () -> Unit = {}

    fun onSuccess(handler: (T) -> Unit) = apply { onSuccess = handler }

    fun onError(handler: (String?) -> Unit) = apply { onError = { msg, _ -> handler(msg) } }

    fun onLoading(handler: () -> Unit) = apply { onLoading = handler }

    fun launch() {
        viewModel.viewModelScope.launch {
            call().collectLatest { result ->
                when (result) {
                    is Result.Loading -> onLoading()
                    is Result.Success -> onSuccess(result.data)
                    is Result.Error -> onError(result.message, result.throwable)
                }
            }
        }
    }
}

fun <T> ViewModel.apiCall(call: () -> Flow<Result<T>>) = ApiCall(this, call)
