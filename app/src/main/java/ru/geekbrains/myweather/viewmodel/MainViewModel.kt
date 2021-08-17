package ru.geekbrains.myweather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Thread.sleep

class MainViewModel(private val liveDataToObserve : MutableLiveData<Any> = MutableLiveData()) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeather() = getDataFromLocalSource()

    fun getData() : LiveData<Any> {
        getDataFromLocalSource()
        return liveDataToObserve
    }

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = AppState.Loading
        Thread{
            sleep(3000)
            liveDataToObserve.postValue(AppState.Seccess(Any()))
        }.start()
    }

}
