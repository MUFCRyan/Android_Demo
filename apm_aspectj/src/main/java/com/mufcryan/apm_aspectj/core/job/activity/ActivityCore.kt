package com.mufcryan.apm_aspectj.core.job.activity

import android.content.pm.ActivityInfo

class ActivityCore {
    companion object{
        private const val SUB_TAG = "TraceActivity"

        var isFirst = true
        var appAttachTime = 0L
        var startType = 0

        private val activityInfo = ActivityInfo()
    }
}