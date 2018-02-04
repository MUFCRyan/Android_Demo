package com.mufcryan.audio_video.tester

import android.os.Environment

abstract class Tester {
    abstract fun startTesting(): Boolean
    abstract fun stopTesting(): Boolean
    companion object {
        val DEFAULT_TEST_DIR = Environment.getExternalStorageDirectory().toString()
        val DEFAULT_TEST_FILE = "testCapture.wav"
    }
}