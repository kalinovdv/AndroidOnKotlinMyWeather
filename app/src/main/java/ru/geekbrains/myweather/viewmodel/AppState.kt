package ru.geekbrains.myweather.viewmodel

sealed class AppState {
    data class Seccess(val weatherData : Any) : AppState()
    data class Error(val error : Throwable) : AppState()
    object Loading : AppState()
}
