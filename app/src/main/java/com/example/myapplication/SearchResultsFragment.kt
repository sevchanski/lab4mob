package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitClient
import com.example.myapplication.databinding.ActivitySearchResultsFragmentBinding
import com.example.myapplication.model.SearchData
import kotlinx.coroutines.launch

class SearchResultsFragment : Fragment() {

    private var _binding: ActivitySearchResultsFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchData: SearchData
    private lateinit var adapter: JobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchData = it.getParcelable("searchData") ?: run {
                Toast.makeText(requireContext(), "Дані пошуку не передано", Toast.LENGTH_SHORT)
                    .show()
                parentFragmentManager.popBackStack()
                return
            }
        } ?: run {
            Toast.makeText(requireContext(), "Дані пошуку не передано", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
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

        adapter = JobAdapter(emptyList()) { job ->
            openJobDetailFragment(job)
        }

        binding.recyclerViewResults.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewResults.adapter = adapter

        fetchJobs()
    }

    private fun fetchJobs() {
        val service = RetrofitClient.api

        val keywords = mutableListOf<String>().apply {
            if (searchData.position.isNotBlank()) add(searchData.position.trim())
            if (searchData.isRemote) add("remote")
        }
        val query = keywords.joinToString(" ").ifBlank { "developer" }
        val location = if (searchData.country.isNotBlank()) searchData.country else null

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = service.getJobs(position = query, location = location)
                val jobs = response.body()?.results ?: emptyList()

                if (jobs.isEmpty()) {
                    Toast.makeText(requireContext(), "Роботи не знайдено", Toast.LENGTH_SHORT)
                        .show()
                }

                adapter.updateJobs(jobs)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Помилка мережі: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun openJobDetailFragment(job: Job) {
        val fragment = JobDetailFragment.newInstance(job)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    inner class JobAdapter(
        private var jobs: List<Job>,
        private val onClick: (Job) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

        inner class JobViewHolder(val binding: com.example.myapplication.databinding.ItemJobBinding) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
            fun bind(job: Job) {
                binding.tvJobTitle.text = job.title
                binding.tvCompanyName.text = job.companyName
                binding.tvJobDescription.text = job.description?.take(200)?.plus(if (job.description.length > 200) "..." else "") ?: "Опис відсутній"
                binding.tvJobUrl.text = job.url
                binding.tvRemote.text = if (job.remote) "Віддалена" else "Офісна"
                binding.root.setOnClickListener {
                    onClick(job)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = com.example.myapplication.databinding.ItemJobBinding.inflate(
                inflater,
                parent,
                false
            )
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

    companion object {
        private const val ARG_SEARCH_DATA = "searchData"

        @JvmStatic
        fun newInstance(data: SearchData) =
            SearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SEARCH_DATA, data)
                }
            }
    }
}
