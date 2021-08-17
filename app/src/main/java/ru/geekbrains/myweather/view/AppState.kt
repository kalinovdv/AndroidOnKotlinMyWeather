package ru.geekbrains.myweather.view

sealed class AppState {
    data class Seccess(val weatherData : Any) : AppState()
    data class Error(val error : Throwable) : AppState()
    object Loading : AppState()
}
