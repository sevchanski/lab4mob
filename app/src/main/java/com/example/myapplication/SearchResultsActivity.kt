package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySearchResultsBinding

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отримання даних
        val position = intent.getStringExtra("position")
        val country = intent.getStringExtra("country")
        val city = intent.getStringExtra("location")
        val remote = intent.getBooleanExtra("remote", false)
        val experience = intent.getIntExtra("experience", 0)
        val employmentType = intent.getStringExtra("employmentType")

        // Формування результату
        val resultsText = """
            Пошук роботи:
            Посада: $position
            Країна: $country
            Місто: $city
            Досвід: $experience років
            Зайнятість: $employmentType
            Віддалено: ${if (remote) "Так" else "Ні"}
        """.trimIndent()

        binding.tvResults.text = resultsText

        // Кнопка "Вибрати роботу"
        binding.btnChooseJob.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("selectedJob", "$position в $city, $country")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
