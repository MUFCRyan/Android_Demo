package com.mufcryan.android_demo.model

import android.os.Handler

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
}

interface OnDataReadyCallback{
    fun onDataReady(data: String)
}