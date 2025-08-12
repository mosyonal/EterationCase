package com.emreonal.eterationcase.ui.component

sealed class UiStatus {
    object Loading : UiStatus()
    data class Error(val message: String) : UiStatus()
    data object ShowContent : UiStatus()
    object Empty : UiStatus()
}