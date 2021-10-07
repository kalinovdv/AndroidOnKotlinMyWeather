package ru.geekbrains.myweather.room

import androidx.room.Database

@Database(entities = arrayOf(HistoryEntity::class), version = 1, exportSchema = false)
abstract class HistoryDataBase {
    abstract fun historyDao(): HistoryDao
}