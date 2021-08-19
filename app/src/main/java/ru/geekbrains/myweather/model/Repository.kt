package ru.geekbrains.myweather.model

interface Repository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalStorageRus() : List<Weather>
    fun getWeatherFromLocalStorageWorld() : List<Weather>
}