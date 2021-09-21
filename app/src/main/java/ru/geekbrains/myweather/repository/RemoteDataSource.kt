package ru.geekbrains.myweather.repository


import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.geekbrains.myweather.BuildConfig

private const val REQUEST_API_KEY = "X-Yandex-API_Key"

class RemoteDataSource {
    fun getWeatherDetails(requestLink: String, callback: Callback) {
        val builder: Request.Builder = Request.Builder().apply {
            header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
            url(requestLink)
        }
        OkHttpClient().newCall(builder.build()).enqueue(callback)
    }
}