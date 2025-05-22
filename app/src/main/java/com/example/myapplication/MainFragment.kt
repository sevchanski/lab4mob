package com.example.myapplication
import com.example.myapplication.model.SearchData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainFragmentBinding

class MainFragment : Fragment() {

    private var _binding: ActivityMainFragmentBinding? = null
    private val binding get() = _binding!!

    private var listener: OnSearchListener? = null

    private val countryCityMap = mapOf(
        "Україна" to listOf("Київ", "Львів", "Одеса"),
        "Польща" to listOf("Варшава", "Краків", "Вроцлав"),
        "Німеччина" to listOf("Берлін", "Мюнхен", "Гамбург")
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSearchListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSearchListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countries = countryCityMap.keys.toList()
        val countryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCountry.adapter = countryAdapter

        // Після вибору країни оновлюємо міста
        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCountry = countries[position]
                updateCitiesSpinner(selectedCountry)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                updateCitiesSpinner(countries.first())
            }
        }

        // SeekBar для досвіду
        binding.seekExperience.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvExperienceValue.text = "$progress років"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSearch.setOnClickListener {
            val positionText = binding.etPosition.text.toString().trim()
            val country = binding.spinnerCountry.selectedItem?.toString() ?: ""
            val city = binding.spinnerCity.selectedItem?.toString() ?: ""
            val experience = binding.seekExperience.progress
            val isRemote = binding.cbRemote.isChecked

            val employmentType = when (binding.rgEmploymentType.checkedRadioButtonId) {
                R.id.rbFullTime -> "Повна"
                R.id.rbPartTime -> "Часткова"
                else -> ""
            }

            if (positionText.isEmpty()) {
                Toast.makeText(requireContext(), "Введіть посаду", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = SearchData(
                position = positionText,
                country = country,
                city = city,
                experience = experience,
                isRemote = isRemote,
                employmentType = employmentType
            )

            listener?.onSearchSubmitted(data)
        }
    }

    private fun updateCitiesSpinner(country: String) {
        val cities = countryCityMap[country] ?: emptyList()
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = cityAdapter

        if (cities.isNotEmpty()) {
            binding.spinnerCity.setSelection(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
