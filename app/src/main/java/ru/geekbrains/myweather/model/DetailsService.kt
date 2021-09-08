package ru.geekbrains.myweather.model

import android.app.IntentService
import android.content.Intent

const val LATITUDE_EXTRA = "Latitude"
const val LONGITUDE_EXTRA = "Longitude"
private const val REQUEST_GET = "GET"
private const val REQUEST_TIMEOUT = 10000
private const val REQUEST_API_KEY = "X-Yandex-API-Key"

class DetailsService(name: String = "DetailsService") : IntentService(name){
    override fun onHandleIntent(intent: Intent?) {

    }
}