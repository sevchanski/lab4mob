package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekExperience.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvExperienceValue.text = "$progress років"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchResultsActivity::class.java).apply {
                putExtra("position", binding.etPosition.text.toString())
                putExtra("location", binding.etLocation.text.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val jobTitle = data?.getStringExtra("selectedJob")
            Toast.makeText(this, "Ви вибрали: $jobTitle", Toast.LENGTH_LONG).show()
        }
    }
}
