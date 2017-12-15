package com.mufcryan.android_demo.view_model

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.mufcryan.android_demo.bean.Repository
import com.mufcryan.android_demo.model.OnDataReadyCallback
import com.mufcryan.android_demo.model.OnRepositoryReadyCallback
import com.mufcryan.android_demo.model.RepoModel

/**
 * Created by zhaofengchun on 2017/12/14.
 *
 */
class MainViewModel: ViewModel() {
    val repoModel = RepoModel()
    val text = ObservableField("Old data")
    val isLoading = ObservableField(false)
    var repositories = ArrayList<Repository>()

    fun refreshData(){
        isLoading.set(true)
        repoModel.refreshData(object : OnDataReadyCallback {
            override fun onDataReady(data: String) {
                isLoading.set(false)
                text.set(data)
            }
        })
    }

    fun loadRespositories(){
        isLoading.set(true)
        repoModel.getRepositories(object : OnRepositoryReadyCallback {
            override fun onDataReady(data: ArrayList<Repository>) {
                isLoading.set(false)
                repositories = data
            }
        })
    }
}