package com.example.myapplication
import com.example.myapplication.model.SearchData

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySearchResultsFragmentBinding

class SearchResultsFragment : Fragment() {

    private var _binding: ActivitySearchResultsFragmentBinding? = null
    private val binding get() = _binding!!

    // Список історії пошуку
    companion object {
        private val searchHistory = mutableListOf<SearchData>()

        private const val ARG_SEARCH_DATA = "search_data"

        fun newInstance(data: SearchData): SearchResultsFragment {
            val fragment = SearchResultsFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_SEARCH_DATA, data)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var currentSearchData: SearchData
    private lateinit var adapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            currentSearchData = it.getParcelable(ARG_SEARCH_DATA)!!
            // Додаємо в історію, якщо такого ще немає
            if (!searchHistory.contains(currentSearchData)) {
                searchHistory.add(0, currentSearchData) // додаємо спереду
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySearchResultsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Відображаємо дані пошуку
        binding.tvResults.text = buildString {
            append("Посада: ${currentSearchData.position}\n")
            append("Країна: ${currentSearchData.country}\n")
            append("Місто: ${currentSearchData.city}\n")
            append("Досвід: ${currentSearchData.experience} років\n")
            append("Зайнятість: ${currentSearchData.employmentType}\n")
            append("Віддалена робота: ${if (currentSearchData.isRemote) "Так" else "Ні"}")
        }

        // Налаштовуємо RecyclerView для історії
        adapter = SearchHistoryAdapter(searchHistory) { selected ->
            Toast.makeText(requireContext(), "Вибрано: ${selected.position} в ${selected.city}", Toast.LENGTH_SHORT).show()

            // При виборі елемента оновлюємо UI
            currentSearchData = selected
            binding.tvResults.text = buildString {
                append("Посада: ${selected.position}\n")
                append("Країна: ${selected.country}\n")
                append("Місто: ${selected.city}\n")
                append("Досвід: ${selected.experience} років\n")
                append("Зайнятість: ${selected.employmentType}\n")
                append("Віддалена робота: ${if (selected.isRemote) "Так" else "Ні"}")
            }
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        // Кнопка "Вибрати роботу"
        binding.btnChooseJob.setOnClickListener {
            Toast.makeText(requireContext(), "Ви вибрали: ${currentSearchData.position} у ${currentSearchData.city}", Toast.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Меню (опціонально)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_results, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
