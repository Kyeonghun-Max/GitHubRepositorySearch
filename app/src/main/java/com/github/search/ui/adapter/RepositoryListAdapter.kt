package com.github.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.search.databinding.RowRepositoryItemBinding
import com.github.search.viewmodel.RepositoryItemViewModel

class RepositoryListAdapter : RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {
    private var items: List<RepositoryItemViewModel>? = null

    fun setItems(items: List<RepositoryItemViewModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryListAdapter.ViewHolder {
        val binding = RowRepositoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepositoryListAdapter.ViewHolder, position: Int) {
        items?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount() = items?.size ?: 0

    inner class ViewHolder(private val binding: RowRepositoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RepositoryItemViewModel) {
            binding.vm = item
        }
    }
}