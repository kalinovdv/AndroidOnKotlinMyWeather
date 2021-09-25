package ru.geekbrains.myweather.repository

import ru.geekbrains.myweather.model.WeatherDTO

interface DetailsRepository {
    fun getWeatherDetailsFromServer(lat: Double, lon: Double, callback: retrofit2.Callback<WeatherDTO>)
}