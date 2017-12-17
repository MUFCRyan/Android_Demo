package com.mufcryan.android_demo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mufcryan.android_demo.bean.Repository
import com.mufcryan.android_demo.databinding.ItemRepositoryBinding

/**
 * Created by zhaofengchun on 2017/12/16.
 *
 */
class RepositoryRecyclerViewAdapter(private var items: ArrayList<Repository>, private var listener: OnItemClickListener): RecyclerView.Adapter<RepositoryRecyclerViewAdapter.ViewHolder>() {
    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder is ViewHolder){
            val repository = items[position]
            return holder.bind(repository, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = ItemRepositoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    fun replaceData(arrayList: ArrayList<Repository>) {
        items = arrayList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(private var binding: ItemRepositoryBinding): RecyclerView.ViewHolder(binding
            .root){
        fun bind(repository: Repository, itemClickListener: OnItemClickListener?){
            binding.repository = repository
            if (itemClickListener != null){
                binding.root.setOnClickListener {
                    itemClickListener.onItemClick(layoutPosition)
                }
            }
            binding.executePendingBindings()
        }
    }
}