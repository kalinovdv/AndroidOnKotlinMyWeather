package ru.geekbrains.myweather.repository

import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.model.getRussianCities
import ru.geekbrains.myweather.model.getWorldCities

class MainRepositoryImpl : MainRepository {
    override fun getWeatherFromServer() = Weather()

    override fun getWeatherFromLocalStorageRus() = getRussianCities()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()
}