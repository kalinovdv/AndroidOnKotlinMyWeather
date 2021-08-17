package ru.geekbrains.myweather.model

data class Weather(
    val city: City = getDefaultCity(),
    val temperature : Int = 0,
    val feelsLike : Int = 0
)

fun getDefaultCity() = City("Ульяновск", 54.32824, 48.38657)
