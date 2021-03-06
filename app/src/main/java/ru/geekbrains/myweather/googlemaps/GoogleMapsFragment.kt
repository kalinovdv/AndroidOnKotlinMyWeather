package ru.geekbrains.myweather.googlemaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_google_maps_main.*
import ru.geekbrains.myweather.R
import ru.geekbrains.myweather.databinding.FragmentGoogleMapsMainBinding
import ru.geekbrains.myweather.model.City
import ru.geekbrains.myweather.model.Weather
import ru.geekbrains.myweather.view.details.DetailsFragment
import java.io.IOException


class GoogleMapsFragment : Fragment() {

    private var _binding: FragmentGoogleMapsMainBinding? = null
    private val binding get() = _binding
    private lateinit var map: GoogleMap
    private val markers: ArrayList<Marker> = arrayListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val initialPlace = LatLng(52.52000659999999, 13.404953999999975)
        googleMap.addMarker(
            MarkerOptions().position(initialPlace).title(getString(R.string.marker_start))
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialPlace))
        googleMap.setOnMapLongClickListener { latLng ->
            getAddressAsync(latLng)
            addMarkerToArray(latLng)
            drawLine()
        }
        googleMap.setOnMapClickListener { latLng ->
            getWeatherOnMap(latLng)
        }
        activateMyLocation(googleMap)
    }

    private fun getWeatherOnMap(location: LatLng) {
        val geocoder = Geocoder(requireContext())
        val addresses =
            geocoder.getFromLocation(location.latitude, location.longitude, 1);
        val name = if (addresses.size > 0) {
            addresses[0].featureName
        } else {
            getString(R.string.t_weather_on_map)
        }
        requireActivity().supportFragmentManager.let {
            with(Bundle()){
                putParcelable(
                    DetailsFragment.BUNDLE_EXTRA,
                    Weather(
                        City( name = name,lat = location.latitude,lon = location.longitude)
                    )
                )
                it.beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(this))
                    .addToBackStack("")
                    .commitAllowingStateLoss()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoogleMapsMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        initSearchByAddress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = GoogleMapsFragment()
    }

    private fun drawLine() {
        val last: Int = markers.size - 1
        if (last > 0) {
            val previous: LatLng = markers[last - 1].position
            val current: LatLng = markers[last].position
            map.addPolyline(
                PolylineOptions()
                    .add(previous, current)
                    .color(Color.RED)
                    .width(5f)
            )
        }
    }

    private fun addMarkerToArray(location: LatLng) {
        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
        markers.add(marker)
    }

    private fun setMarker(location: LatLng, searchText: String, resourceId: Int): Marker {
        return map.addMarker(
            MarkerOptions()
                .position(location)
                .title(searchText)
                .icon(BitmapDescriptorFactory.fromResource(resourceId))
        )
    }

    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)
            Thread {
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    textAddress.post {
                        textAddress.text = addresses[0].getAddressLine(0)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun initSearchByAddress() {
        binding?.buttonSearch?.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = searchAddress.text.toString()
            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(searchText, 1)
                    if (addresses.size > 0) {
                        goToAddress(addresses, it, searchText)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun goToAddress(addresses: MutableList<Address>, view: View, searchText: String) {
        val location = LatLng(addresses[0].latitude, addresses[0].longitude)
        view.post {
            setMarker(location, searchText, R.drawable.ic_map_pin)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun activateMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            googleMap.isMyLocationEnabled = isPermissionGranted
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
        }
    }

}