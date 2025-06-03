package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainFragmentBinding // <--- ДОДАЙТЕ ЦЕЙ ІМПОРТ
import com.example.myapplication.model.SearchData

class MainFragment : Fragment() {

    // private var _binding: ActivitySearchResultsFragmentBinding? = null // <-- ЗМІНИТИ ТИП
    private var _binding: ActivityMainFragmentBinding? = null // <--- ПРАВИЛЬНИЙ ТИП BINDING
    private val binding get() = _binding!!

    private val countries = listOf("Україна", "Польща", "Німеччина", "США") // приклад
    private val citiesByCountry = mapOf(
        "Україна" to listOf("Київ", "Львів", "Одеса"),
        "Польща" to listOf("Варшава", "Краків"),
        "Німеччина" to listOf("Берлін", "Мюнхен"),
        "США" to listOf("Нью-Йорк", "Сан-Франциско")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // _binding = ActivitySearchResultsFragmentBinding.inflate(inflater, container, false) // <-- ЗМІНИТИ ВИКЛИК
        _binding = ActivityMainFragmentBinding.inflate(inflater, container, false) // <--- ПРАВИЛЬНИЙ ВИКЛИК
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCountrySpinner()
        setupListeners()
    }

    private fun setupCountrySpinner() {
        val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCountry.adapter = countryAdapter

        // За замовчуванням оновити міста відповідно до першої країни
        updateCitySpinner(countries.first())

        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCountry = countries[position]
                updateCitySpinner(selectedCountry)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateCitySpinner(country: String) {
        val cities = citiesByCountry[country] ?: emptyList()
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = cityAdapter
    }

    private fun setupListeners() {
        // Оновлення тексту досвіду з SeekBar
        binding.seekExperience.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvExperienceValue.text = "$progress років"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.btnSearch.setOnClickListener {
            val positionText = binding.etPosition.text.toString().trim()
            if (positionText.isEmpty()) {
                Toast.makeText(requireContext(), "Введіть посаду", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            // Передача даних і заміна фрагмента
            val fragment = SearchResultsFragment.newInstance(searchData) // <--- ВИКОРИСТОВУЙТЕ newInstance
            // (Який ми додали в попередньому кроці)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // <--- Змінено на fragment_container
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}