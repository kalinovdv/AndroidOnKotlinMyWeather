package ru.geekbrains.myweather.model

interface Repository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalStorage() : Weather
}