package ru.geekbrains.myweather.repository

import ru.geekbrains.myweather.model.Weather

interface MainRepository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalStorageRus() : List<Weather>
    fun getWeatherFromLocalStorageWorld() : List<Weather>
}