package ru.geekbrains.myweather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.geekbrains.myweather.model.RepositoryImpl

class MainViewModel(private val liveDataToObserve : MutableLiveData<Any> = MutableLiveData(),
                    private val repositoryImpl: RepositoryImpl = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSource() = getDataFromLocalSource()
    fun getWeatherFromRemoteSource() = getDataFromLocalSource()

    fun getData() : LiveData<Any> {
        getDataFromLocalSource()
        return liveDataToObserve
    }

    private fun getDataFromLocalSource() {
        val stateRandom = (0..4).random()
        when (stateRandom){
            1 -> liveDataToObserve.value = AppState.Loading
            2 -> liveDataToObserve.value = AppState.Seccess(repositoryImpl.getWeatherFromLocalStorage())
            3 -> {/*обработка ошибок*/}
        }
        /*liveDataToObserve.value = AppState.Loading
        Thread{
            sleep(3000)
            liveDataToObserve.postValue(AppState.Seccess(repositoryImpl.getWeatherFromLocalStorage()))
        }.start()*/
    }

}
