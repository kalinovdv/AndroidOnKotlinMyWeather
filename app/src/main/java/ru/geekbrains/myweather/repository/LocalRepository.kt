package ru.geekbrains.myweather.repository

import ru.geekbrains.myweather.model.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun saveEntity(weather: Weather)
}