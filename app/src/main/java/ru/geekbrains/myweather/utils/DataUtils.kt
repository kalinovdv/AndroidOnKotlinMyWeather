package ru.geekbrains.myweather.utils

import ru.geekbrains.myweather.model.FactDTO
import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.model.WeatherDTO
import ru.geekbrains.myweather.model.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): Weather {
    val fact: FactDTO = weatherDTO.fact!!
    return Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition, fact.icon)
}
