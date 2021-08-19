/*
package ru.geekbrains.myweather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.geekbrains.myweather.R
import ru.geekbrains.myweather.databinding.MainFragmentBinding
import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.viewmodel.AppState
import ru.geekbrains.myweather.viewmodel.MainViewModel

class DetailsFragment : Fragment() {

    private var _binding : MainFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getData().observe(viewLifecycleOwner, {renderData(it as AppState)})
        viewModel.getWeatherFromLocalSource()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(appState: AppState) {
        when(appState) {
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Seccess -> {
                val weatherData = appState.weatherData
                binding.loadingLayout.visibility = View.GONE
                setData(weatherData)
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(binding.mainView, "Ошибка загрузки", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Перезагрузка") { viewModel.getWeatherFromLocalSource()}
                    .show()
            }
        }
    }

    private fun setData(weatherData: Weather) {
        binding.cityName.text = weatherData.city.city
        binding.cityCoordinates.text = String.format(getString(R.string.city_coordinates),
            weatherData.city.lat.toString(),
            weatherData.city.lon.toString())
        binding.temperatureValue.text = weatherData.temperature.toString()
        binding.feelsLikeValue.text = weatherData.feelsLike.toString()
    }

}*/
