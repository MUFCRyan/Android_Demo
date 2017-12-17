package com.mufcryan.android_demo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.mufcryan.android_demo.adapter.RepositoryRecyclerViewAdapter
import com.mufcryan.android_demo.bean.Repository
import com.mufcryan.android_demo.databinding.ActivityMainBinding
import com.mufcryan.android_demo.view_model.MainViewModel

class MainActivity : AppCompatActivity(), RepositoryRecyclerViewAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private var mAdapter = RepositoryRecyclerViewAdapter(arrayListOf(), this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(MainViewModel().javaClass)
        binding.viewModel = viewModel
        binding.executePendingBindings()

        binding.rvRecycler.layoutManager = LinearLayoutManager(this)
        binding.rvRecycler.adapter = mAdapter
        viewModel.repositories.observe(this, Observer<ArrayList<Repository>> {
            it?.let {
                mAdapter.replaceData(it)
            }
        })
    }

    override fun onItemClick(position: Int) {
    }
}
