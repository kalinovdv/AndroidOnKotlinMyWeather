package ru.geekbrains.myweather.view.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_main.*
import ru.geekbrains.myweather.R
import ru.geekbrains.myweather.databinding.FragmentMainBinding
import ru.geekbrains.myweather.model.City
import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.view.contentprovider.REQUES_CODE
import ru.geekbrains.myweather.view.details.DetailsFragment
import ru.geekbrains.myweather.viewmodel.AppState
import ru.geekbrains.myweather.viewmodel.MainViewModel
import showSnackBar
import java.io.IOException

private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: MainViewModel
    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            openDetailsFragment(weather)
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
        binding?.mainFragmentFABLocation?.setOnClickListener { checkPermission() }
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java).also { it ->
            it.getLiveData().observe(viewLifecycleOwner, { renderData(it as AppState) })
            it.getWeatherFromLocalSourceRus()
        }
    }

    private fun checkPermission() {
        Toast.makeText(activity, "Доступно в платной версии", Toast.LENGTH_LONG).show()
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUES_CODE)
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_meaasge))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ -> requestPermission() }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(getString(R.string.dialog_title_gps_turned_off), getString(R.string.dialog_message_last_location_unknown))
                    } else {
                        getAddressAsync(context, location)
                        showDialog(getString(R.string.dialog_title_gps_turned_off), getString(R.string.dialog_message_last_known_location))
                    }
                }
            } else {
                showRationaleDialog()
            }
        }
    }

    private val onLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //super.onStatusChanged(provider, status, extras)
        }

        override fun onProviderEnabled(provider: String) {
            //super.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
            //super.onProviderDisabled(provider)
        }
    }

    private fun getAddressAsync(context: Context, location: Location) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                mainFragmentFAB.post {
                    showAddressDialog(address[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) {_,_ -> openDetailsFragment(Weather(
                    City(address, location.latitude, location.longitude)
                ))}
                .setNegativeButton(getString(R.string.dialog_button_close)) {dialog,_ -> dialog.dismiss()}
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(weather: Weather) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        checkPermissinsResult(requestCode, grantResults)
    }

    private fun checkPermissinsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUES_CODE -> {
                var grantedPermission = 0
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermission++
                        }
                    }
                    if (grantResults.size == grantedPermission) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }
                return
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
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
                binding?.mainFragmentRootView?.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getWeatherFromLocalSourceRus() }
                )
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
