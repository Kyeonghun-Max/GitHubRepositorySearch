package com.github.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.github.search.databinding.RowRepositoryItemBinding
import com.github.search.databinding.RowRepositoryLoadingBinding
import com.github.search.viewmodel.ItemViewModel
import com.github.search.viewmodel.LoadingViewModel
import javax.inject.Inject

class RepositoryListAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var items: ArrayList<ViewModel>? = ArrayList()

    fun setItems(items: ArrayList<ViewModel>) {
        this.items?.let {
            it.clear()
            it.addAll(items)
            notifyDataSetChanged()
        }
    }

    fun addItems(addedItems: ArrayList<ViewModel>) {
        items?.let {
            it.indexOfLast { vm -> vm is LoadingViewModel }.let { indexOfLoadingVm ->
                if (indexOfLoadingVm > -1) {
                    it.removeAt(indexOfLoadingVm)
                    notifyItemRemoved(indexOfLoadingVm)
                }
            }
            val prevSize = it.size
            it.addAll(addedItems)
            notifyItemRangeInserted(prevSize, addedItems.size)
        }
    }

    fun removeLoadingVm() {
        items?.let {
            it.indexOfLast { vm -> vm is LoadingViewModel }.let { indexOfLoadingVm ->
                if (indexOfLoadingVm > -1) {
                    it.removeAt(indexOfLoadingVm)
                    notifyItemRemoved(indexOfLoadingVm)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = RowRepositoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        } else {
            val binding = RowRepositoryLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemViewHolder) {
            items?.let {
                holder.bind(it[position] as ItemViewModel)
            }
        }
    }

    override fun getItemCount() = items?.size ?: 0

    fun getRealItemCount(): Int {
        return items?.filterIsInstance<ItemViewModel>()?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return items?.get(position)?.let {
            if (it is ItemViewModel) {
                VIEW_TYPE_ITEM
            } else {
                VIEW_TYPE_LOADING
            }
        } ?: VIEW_TYPE_LOADING
    }

    inner class ItemViewHolder(private val binding: RowRepositoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemViewModel) {
            binding.vm = item
        }
    }

    inner class LoadingViewHolder(binding: RowRepositoryLoadingBinding) : RecyclerView.ViewHolder(binding.root)
}