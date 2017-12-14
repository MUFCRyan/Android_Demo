package com.mufcryan.android_demo.view_model

import android.databinding.ObservableField
import com.mufcryan.android_demo.model.OnDataReadyCallback
import com.mufcryan.android_demo.model.RepoModel

/**
 * Created by zhaofengchun on 2017/12/14.
 *
 */
class MainViewModel {
    val repoModel = RepoModel()
    val text = ObservableField("Old data")
    val isLoading = ObservableField(false)

    fun refreshData(){
        isLoading.set(true)
        repoModel.refreshData(object : OnDataReadyCallback {
            override fun onDataReady(data: String) {
                isLoading.set(false)
                text.set(data)
            }
        })
    }
}