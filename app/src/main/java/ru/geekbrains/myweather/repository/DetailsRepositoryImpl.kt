package ru.geekbrains.myweather.repository

import ru.geekbrains.myweather.model.WeatherDTO

class DetailsRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsRepository {
    override fun getWeatherDetailsFromServer(lat: Double, lon:Double, callback: retrofit2.Callback<WeatherDTO>) {
        remoteDataSource.getWeatherDetails(lat, lon, callback)
    }
}