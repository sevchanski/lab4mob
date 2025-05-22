package com.example.myapplication

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySearchResultsFragmentBinding
import com.example.myapplication.model.SearchData

class SearchResultsFragment : Fragment() {

    private var _binding: ActivitySearchResultsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentSearchData: SearchData
    private lateinit var adapter: SearchHistoryAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            currentSearchData = it.getParcelable(ARG_SEARCH_DATA)!!
            if (!searchHistory.contains(currentSearchData)) {
                searchHistory.add(0, currentSearchData)
            }
        }
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

        displaySearchData(currentSearchData)

        adapter = SearchHistoryAdapter(searchHistory) { selected ->
            Toast.makeText(requireContext(), "Вибрано: ${selected.position} в ${selected.city}", Toast.LENGTH_SHORT).show()
            currentSearchData = selected
            displaySearchData(selected)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        binding.btnChooseJob.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Ви вибрали: ${currentSearchData.position} у ${currentSearchData.city}",
                Toast.LENGTH_LONG
            ).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun displaySearchData(data: SearchData) {
        binding.tvResults.text = buildString {
            append("Посада: ${data.position}\n")
            append("Країна: ${data.country}\n")
            append("Місто: ${data.city}\n")
            append("Досвід: ${data.experience} років\n")
            append("Зайнятість: ${data.employmentType}\n")
            append("Віддалена робота: ${if (data.isRemote) "Так" else "Ні"}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_results, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
