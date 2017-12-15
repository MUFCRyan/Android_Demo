package com.mufcryan.android_demo.model

import android.os.Handler
import com.mufcryan.android_demo.bean.Repository

/**
 * Created by zhaofengchun on 2017/12/14.
 *
 */
class RepoModel {
    fun refreshData(callback: OnDataReadyCallback){
        Handler().postDelayed({
            callback.onDataReady("Some new data")
        }, 2000)
    }

    fun getRepositories(onRepositoryReadyCallback: OnRepositoryReadyCallback) {
        val arrayList = ArrayList<Repository>()
        arrayList.add(Repository("First", "Owner 1", 100 , false))
        arrayList.add(Repository("Second", "Owner 2", 30 , true))
        arrayList.add(Repository("Third", "Owner 3", 430 , false))

        Handler().postDelayed({ onRepositoryReadyCallback.onDataReady(arrayList) },2000)
    }
}

interface OnDataReadyCallback{
    fun onDataReady(data: String)
}

interface OnRepositoryReadyCallback {
    fun onDataReady(data : ArrayList<Repository>)
}