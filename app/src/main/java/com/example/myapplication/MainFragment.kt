package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.PrefsManager
import com.example.myapplication.data.SearchEntity
import com.example.myapplication.databinding.ActivityMainFragmentBinding
import com.example.myapplication.model.SearchData
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
private var _binding: ActivityMainFragmentBinding? = null
private val binding get() = _binding!!

private val countries = listOf("Poland", "Germany", "USA")
private val citiesByCountry = mapOf(
    "Poland" to listOf("Warsaw", "Krakow"),
    "Germany" to listOf("Berlin", "Munich"),
    "USA" to listOf("New York", "San Francisco")
)

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    _binding = ActivityMainFragmentBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setupCountrySpinner()
    setupListeners()
    loadFromPrefs()
}

private fun setupCountrySpinner() {
    val countryAdapter = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_item,
        countries
    )
    countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.spinnerCountry.adapter = countryAdapter

    updateCitySpinner(countries.first())

    binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val selectedCountry = countries[position]
            updateCitySpinner(selectedCountry)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

private fun updateCitySpinner(country: String) {
    val cities = citiesByCountry[country] ?: emptyList()
    val cityAdapter = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_item,
        cities
    )
    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.spinnerCity.adapter = cityAdapter
}

private fun setupListeners() {
    binding.seekExperience.setOnSeekBarChangeListener(object :
        android.widget.SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: android.widget.SeekBar?,
            progress: Int,
            fromUser: Boolean
        ) {
            binding.tvExperienceValue.text = "$progress років"
        }

        override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
    })

    binding.btnSearch.setOnClickListener {
        val positionText = binding.etPosition.text.toString().trim()
        val selectedCountry = binding.spinnerCountry.selectedItem?.toString() ?: ""
        val selectedCity = binding.spinnerCity.selectedItem?.toString() ?: ""

        val employmentType = when (binding.rgEmploymentType.checkedRadioButtonId) {
            R.id.rbFullTime -> "Повна"
            R.id.rbPartTime -> "Часткова"
            else -> ""
        }

        val searchData = SearchData(
            position = positionText,
            country = selectedCountry,
            city = selectedCity,
            isRemote = binding.cbRemote.isChecked,
            experience = binding.seekExperience.progress,
            employmentType = employmentType
        )

        // Зберегти в SharedPreferences
        val prefs = PrefsManager(requireContext())
        prefs.saveLastSearch(positionText)

        // Зберегти в Room
        val entity = SearchEntity(
            position = positionText,
            country = selectedCountry,
            city = selectedCity,
            isRemote = binding.cbRemote.isChecked,
            experience = binding.seekExperience.progress,
            employmentType = employmentType
        )
        lifecycleScope.launch {
            AppDatabase.getDatabase(requireContext())
                .searchDao()
                .insertSearch(entity)
        }

        // Перейти до SearchResultsFragment
        val fragment = SearchResultsFragment.newInstance(searchData)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    binding.btnHistory.setOnClickListener {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HistoryFragment())
            .addToBackStack(null)
            .commit()
    }
}

private fun loadFromPrefs() {
    val prefs = PrefsManager(requireContext())
    val last = prefs.getLastSearch()
    binding.etPosition.setText(last)
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null}
}
