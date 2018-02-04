package com.mufcryan.audio_video.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mufcryan.audio_video.R
import com.mufcryan.audio_video.tester.AudioCaptureTester
import com.mufcryan.audio_video.tester.AudioCodecTester
import com.mufcryan.audio_video.tester.AudioPlayerTester
import com.mufcryan.audio_video.tester.Tester
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mTester: Tester
    companion object {
        val TEST_PROGRAM_ARRAY = arrayOf("录制 wav 文件", "播放 wav 文件", "OpenSL ES 录制", "OpenSL ES 播放", "音频编解码")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        verifyStoragePermissions(this)
    }

    private fun init() {
        btnStartTest.setOnClickListener { startTest() }
        btnStopTest.setOnClickListener { stopTest() }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TEST_PROGRAM_ARRAY)
        testSpinner.adapter = adapter
    }

    private fun startTest() {
        when(testSpinner.selectedItemPosition){
            0 -> mTester = AudioCaptureTester()
            1 -> mTester = AudioPlayerTester()
            4 -> mTester = AudioCodecTester()
        }

        if (mTester != null){
            mTester.startTesting()
            Toast.makeText(this, "Starting test !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopTest() {
        if (mTester != null){
            mTester.stopTesting()
            Toast.makeText(this, "Stop test !", Toast.LENGTH_SHORT).show()

        }
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE)
        }
    }
}
