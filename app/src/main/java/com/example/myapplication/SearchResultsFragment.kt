package com.example.myapplication // Пакет вашого додатка

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySearchResultsFragmentBinding // АБО com.example.myapplication.databinding.ActivitySearchResultsFragmentBinding, якщо так називається ваш макет
import com.example.myapplication.model.SearchData
import com.example.myapplication.api.RetrofitClient // ІМПОРТ: Ваш глобальний RetrofitClient

// ІМПОРТИ ДЛЯ KOTLIN COROUTINES
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SearchResultsFragment : Fragment() {

    // Примітка: Використовуйте FragmentSearchResultsBinding, якщо ваш макет fragment_search_results.xml
    private var _binding: ActivitySearchResultsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchData: SearchData
    private lateinit var adapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Ключ "searchData" має співпадати з тим, що ви передаєте з MainFragment
            searchData = it.getParcelable("searchData") ?: run {
                Toast.makeText(requireContext(), "Дані пошуку не передано", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Повертаємося назад, якщо дані відсутні
                return
            }
        } ?: run {
            Toast.makeText(requireContext(), "Дані пошуку не передано", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack() // Повертаємося назад
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Примітка: Використовуйте FragmentSearchResultsBinding.inflate
        _binding = ActivitySearchResultsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = JobAdapter(emptyList()) { job ->
            // Відкриваємо посилання у браузері
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.url))
            startActivity(intent)
        }

        binding.recyclerViewResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewResults.adapter = adapter

        fetchJobs()
    }

    private fun fetchJobs() {
        // *** НАЙВАЖЛИВІША ЗМІНА: ВИКОРИСТАННЯ ГЛОБАЛЬНОГО RetrofitClient ***
        // Повністю ВИДАЛІТЬ ВЕСЬ КОД, ЯКИЙ ІНІЦІАЛІЗУЄ OkHttpClient, Retrofit та FindWorkApi В ЦЬОМУ МЕТОДІ
        // Натомість використовуйте:
        val service = RetrofitClient.api

        // Формуємо пошуковий рядок
        val keywords = mutableListOf<String>()
        if (searchData.position.isNotEmpty()) keywords.add(searchData.position)
        if (searchData.isRemote) keywords.add("remote")
        if (searchData.employmentType.lowercase().contains("повна")) keywords.add("fulltime")
        if (searchData.employmentType.lowercase().contains("часткова")) keywords.add("parttime")
        val query = keywords.joinToString(" ")

        // Локація (місто + країна)
        val location = if (searchData.city.isNotEmpty()) "${searchData.city}, ${searchData.country}" else searchData.country

        // *** ВИКОРИСТАННЯ KOTLIN COROUTINES ДЛЯ АСИНХРОННОГО ВИКЛИКУ API ***
        viewLifecycleOwner.lifecycleScope.launch { // Запускаємо корутину в контексті життєвого циклу фрагмента
            try {
                // Викликаємо suspend функцію з FindWorkApi
                val response = service.getJobs(query, location)
                val jobs = response.body()?.results ?: emptyList()


                if (jobs.isEmpty()) {
                    Toast.makeText(requireContext(), "Роботи не знайдено", Toast.LENGTH_SHORT).show()
                }
                adapter.updateJobs(jobs)
            } catch (e: Exception) {
                // Обробка помилок мережі або інших винятків
                Toast.makeText(requireContext(), "Помилка мережі: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                e.printStackTrace() // Для налагодження: вивести стек викликів у Logcat
            }
        }
    }

    // Вкладений адаптер для RecyclerView (залишається як є, з вашого коду)
    inner class JobAdapter(
        private var jobs: List<Job>,
        private val onClick: (Job) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

        inner class JobViewHolder(val binding: com.example.myapplication.databinding.ItemJobBinding) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
            fun bind(job: Job) {
                binding.tvJobTitle.text = job.title
                binding.tvCompanyName.text = job.companyName // Використовуйте companyName (якщо ви оновили Job.kt)
                // Обрізаємо опис, якщо він занадто довгий
                binding.tvJobDescription.text = job.description.take(200) + if (job.description.length > 200) "..." else ""
                binding.tvJobUrl.text = job.url
                binding.tvRemote.text = if (job.remote) "Віддалена" else "Офісна"
                binding.root.setOnClickListener {
                    onClick(job)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = com.example.myapplication.databinding.ItemJobBinding.inflate(inflater, parent, false)
            return JobViewHolder(binding)
        }

        override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
            holder.bind(jobs[position])
        }

        override fun getItemCount() = jobs.size

        fun updateJobs(newJobs: List<Job>) {
            jobs = newJobs
            notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // *** ВАЖЛИВО: ЦЕЙ БЛОК 'companion object' ПОТРІБНО ДОДАТИ ***
    // Він створює екземпляр фрагмента і передає йому аргументи (SearchData)
    companion object {
        // Цей ключ має співпадати з тим, що ви використовуєте для отримання даних в onCreate()
        private const val ARG_SEARCH_DATA = "searchData"

        @JvmStatic // Робить метод доступним як статичний з Java (і Kotlin)
        fun newInstance(data: SearchData) =
            SearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SEARCH_DATA, data) // data є Parcelable, тому putParcelable
                }
            }
    }
}