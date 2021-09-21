package ru.geekbrains.myweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweather.repository.MainRepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<Any> = MutableLiveData(),
    private val repositoryImpl: MainRepositoryImpl = MainRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(true)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(false)
    fun getWeatherFromRemoteSource() = getDataFromLocalSource(true)

    private fun getDataFromLocalSource(isRussian: Boolean) {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(2000)
            liveDataToObserve.postValue(
                AppState.Seccess(
                    if (isRussian) repositoryImpl.getWeatherFromLocalStorageRus()
                    else repositoryImpl.getWeatherFromLocalStorageWorld()
                )
            )
        }.start()
    }
}
