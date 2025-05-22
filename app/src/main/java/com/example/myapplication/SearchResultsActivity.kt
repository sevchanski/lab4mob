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

        val position = intent.getStringExtra("position")
        val location = intent.getStringExtra("location")
        val employmentType = intent.getStringExtra("employmentType")
        val remote = intent.getBooleanExtra("remote", false)
        val experience = intent.getIntExtra("experience", 0)

        binding.tvResults.text = """
            Результати пошуку:
            Посада: $position
            Місто: $location
            Тип: $employmentType
            Віддалена: ${if (remote) "Так" else "Ні"}
            Досвід: $experience років
        """.trimIndent()

        binding.btnChooseJob.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("selectedJob", "$position в $location")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
