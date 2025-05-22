package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val countryCityMap = mapOf(
        "Україна" to listOf("Київ", "Львів", "Харків", "Одеса"),
        "Польща" to listOf("Варшава", "Краків", "Гданськ"),
        "Німеччина" to listOf("Берлін", "Мюнхен", "Гамбург")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SeekBar: показувати значення досвіду
        binding.seekExperience.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvExperienceValue.text = "$progress років"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Заповнення Spinner для країн
        val countries = countryCityMap.keys.toList()
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCountry.adapter = countryAdapter

        // Встановлення міст для першої країни за замовчуванням
        updateCitySpinner(countries.first())

        // Слухач вибору країни
        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCountry = countries[position]
                updateCitySpinner(selectedCountry)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Кнопка пошуку
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchResultsActivity::class.java).apply {
                putExtra("position", binding.etPosition.text.toString())
                putExtra("country", binding.spinnerCountry.selectedItem.toString())
                putExtra("location", binding.spinnerCity.selectedItem.toString())
                putExtra("remote", binding.cbRemote.isChecked)
                putExtra("experience", binding.seekExperience.progress)
                putExtra("employmentType", when (binding.rgEmploymentType.checkedRadioButtonId) {
                    R.id.rbFullTime -> "Повна"
                    R.id.rbPartTime -> "Часткова"
                    else -> ""
                })
            }
            startActivityForResult(intent, 1)
        }
    }

    private fun updateCitySpinner(country: String) {
        val cities = countryCityMap[country] ?: emptyList()
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = cityAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val jobTitle = data?.getStringExtra("selectedJob")
            Toast.makeText(this, "Ви вибрали: $jobTitle", Toast.LENGTH_LONG).show()
        }
    }
}
