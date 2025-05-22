package com.example.myapplication
import com.example.myapplication.model.SearchData

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivitySearchHistoryAdapterBinding

class SearchHistoryAdapter(
    private val items: List<SearchData>,
    private val onItemClick: (SearchData) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ActivitySearchHistoryAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchData) {
            binding.tvHistoryItem.text = "${item.position} - ${item.city}, ${item.country}"
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActivitySearchHistoryAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
