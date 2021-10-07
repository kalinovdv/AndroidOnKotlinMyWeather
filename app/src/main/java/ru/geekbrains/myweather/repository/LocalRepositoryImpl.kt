package ru.geekbrains.myweather.repository

import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.room.HistoryDao
import ru.geekbrains.myweather.utils.convertWeatherToEntity
import ru.geekbrains.myweather.utils.convertHistotyEntityToWeather

class LocalRepositoryImpl(private val localDatSource: HistoryDao) : LocalRepository {

    override fun getAllHistory(): List<Weather> {
        return convertHistotyEntityToWeather(localDatSource.all())
    }

    override fun saveEntity(weather: Weather) {
        localDatSource.insert(convertWeatherToEntity(weather))
    }

}