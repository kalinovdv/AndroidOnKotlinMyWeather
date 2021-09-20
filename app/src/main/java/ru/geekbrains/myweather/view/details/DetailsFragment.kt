package ru.geekbrains.myweather.view.details

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import okhttp3.*
import ru.geekbrains.myweather.BuildConfig
import ru.geekbrains.myweather.R
import ru.geekbrains.myweather.databinding.FragmentDetailsBinding
import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.model.WeatherDTO
import java.io.IOException

private const val REQUEST_API_KEY = "X-Yandex-API-Key"
private const val MAIN_LINK = "https://api.weather.yandex.ru/v2/informers?"
private const val PROCESS_ERROR = "Обработка ошибки"

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        getWeather()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getWeather() {
        with(binding) {
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE

            val client = OkHttpClient()
            val builder: Request.Builder = Request.Builder()
            builder.header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
            builder.url(MAIN_LINK + "lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
            val request: Request = builder.build()
            val call: Call = client.newCall(request)
            call.enqueue(object : Callback {
                val handler: Handler = Handler()

                override fun onFailure(call: Call, e: IOException) {
                    TODO(PROCESS_ERROR)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val serverResponse: String? = response.body()?.string()
                    if (response.isSuccessful && serverResponse != null) {
                        handler.post { renderData(Gson().fromJson(serverResponse, WeatherDTO::class.java)) }
                    } else {
                        TODO(PROCESS_ERROR)
                    }
                }
            })
        }
    }

    private fun renderData(weatherDTO: WeatherDTO) {
        with(binding) {
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
            val fact = weatherDTO.fact
            if (fact == null || fact.temp == null || fact.feels_like == null || fact.condition.isNullOrEmpty()) {
                TODO(PROCESS_ERROR)
            } else {
                val city = weatherBundle.city
                cityName.text = city.name
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    city.lat.toString(),
                    city.lon.toString()
                )
                weatherCondition.text = fact.condition
                temperatureValue.text = fact.temp.toString()
                feelsLikeValue.text = fact.feels_like.toString()
            }
        }
    }

    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
