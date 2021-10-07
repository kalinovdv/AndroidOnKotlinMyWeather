package ru.geekbrains.myweather.utils

import ru.geekbrains.myweather.model.*
import ru.geekbrains.myweather.room.HistoryEntity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    val fact: FactDTO = weatherDTO.fact!!
    return Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition, fact.icon)
}

fun convertHistotyEntityToWeather(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map { Weather(City(it.city, 0.0, 0.0), it.temperature, 0, it.condition) }
}

fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity(0, weather.city.name, weather.temperature, weather.condition)
}