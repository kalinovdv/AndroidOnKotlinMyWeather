package ru.geekbrains.myweather.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.geekbrains.myweather.R
import ru.geekbrains.myweather.databinding.FragmentMainBinding
import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.view.details.DetailsFragment
import ru.geekbrains.myweather.viewmodel.AppState
import ru.geekbrains.myweather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: MainViewModel
    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            activity?.supportFragmentManager.let {
                with(Bundle()) {
                    putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    it?.beginTransaction()
                        ?.add(R.id.container, DetailsFragment.newInstance(this))
                        ?.addToBackStack("")
                        ?.commitAllowingStateLoss()
                }
            }
        }
    })

    private var isDataSetRus: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mainFragmentRecyclerView?.adapter = adapter
        binding?.mainFragmentFAB?.setOnClickListener { changeWeatherDataSet() }
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java).also {
            it.getLiveData().observe(viewLifecycleOwner, { renderData(it as AppState) })
            it.getWeatherFromLocalSourceRus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.removeListener()
        _binding = null
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Loading -> {
                binding?.mainFragmentLoadingLayout?.visibility = View.VISIBLE
            }
            is AppState.Seccess -> {
                binding?.mainFragmentLoadingLayout?.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Error -> {
                binding?.mainFragmentLoadingLayout?.visibility = View.GONE
                Snackbar
                    .make(
                        binding?.mainFragmentFAB!!, getString(R.string.error),
                        Snackbar.LENGTH_INDEFINITE
                    )
                    .setAction(getString(R.string.reload)) {
                        viewModel.getWeatherFromLocalSourceRus()
                    }
                    .show()
            }
        }
    }

    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding?.mainFragmentFAB?.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding?.mainFragmentFAB?.setImageResource(R.drawable.ic_russia)
        }.also { isDataSetRus = !isDataSetRus }
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }
}
