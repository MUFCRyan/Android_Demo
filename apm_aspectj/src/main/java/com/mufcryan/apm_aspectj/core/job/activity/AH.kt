package com.mufcryan.apm_aspectj.core.job.activity

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.text.TextUtils
import java.time.chrono.IsoEra

class AH {
    companion object {
        const val SUB_TAG = "TraceActivity"

        fun applicationOnCreate(context: Context){

        }

        fun applicationAttachBaseContext(context: Context){
            ActivityCore.appAttachTime = System.currentTimeMillis()
        }

        fun invoke(activity: Activity, startTime: Long, lifecycle: String, sign: String){
            val isRunning = isActivityTaskRunning()
            if(!isRunning){
                return
            }
            /*if(TextUtils.equals(lifecycle, ActivityInfo.)){

            } else {

            }*/
        }

        fun isActivityTaskRunning(): Boolean{
            return false
        }
    }
}