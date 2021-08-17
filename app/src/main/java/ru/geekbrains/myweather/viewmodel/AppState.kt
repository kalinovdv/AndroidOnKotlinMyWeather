package ru.geekbrains.myweather.viewmodel

import ru.geekbrains.myweather.model.Weather

sealed class AppState {
    data class Seccess(val weatherData : Weather) : AppState()
    data class Error(val error : Throwable) : AppState()
    object Loading : AppState()
}
