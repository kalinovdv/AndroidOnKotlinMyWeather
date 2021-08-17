package ru.geekbrains.myweather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.geekbrains.myweather.databinding.MainFragmentBinding
import ru.geekbrains.myweather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding : MainFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
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
        val observer = Observer<Any> { renderData(it)}
        viewModel.getData().observe(viewLifecycleOwner, observer)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(data: Any?) {
        Toast.makeText(context, "Данные получены", Toast.LENGTH_LONG).show()
    }

}