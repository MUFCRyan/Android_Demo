package com.mufcryan.audio_video.tester

import android.media.AudioFormat
import android.media.MediaRecorder
import com.mufcryan.audio_video.api.audio.AudioCapture
import com.mufcryan.audio_video.api.wav.WavFileWriter
import java.io.File
import java.io.IOException

class AudioCaptureTester: Tester(), AudioCapture.OnAudioFrameCapturedListener{
    private var mAudioCapture: AudioCapture? = null
    private var mWavFileWriter: WavFileWriter? = null

    override fun startTesting(): Boolean {
        mAudioCapture = AudioCapture()
        mWavFileWriter = WavFileWriter()
        try {
            val dir = File(DEFAULT_TEST_DIR)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(dir, DEFAULT_TEST_FILE)
            if (!file.exists()){
                file.createNewFile()
                file.setWritable(true)
                file.setReadable(true)
            }
            mWavFileWriter!!.openFile(file.path, 44100, 1, 16)
        } catch (e: IOException){
            e.printStackTrace()
            return false
        }

        mAudioCapture!!.setOnAudioFrameCapturedListener(this)
        return mAudioCapture!!.startCapture(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    }

    override fun stopTesting(): Boolean {
        mAudioCapture!!.stopCapture()
        try {
            mWavFileWriter!!.closeFile()
        } catch (e: IOException){
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun onAudioFrameCaptured(audioData: ByteArray) {
        mWavFileWriter!!.writeData(audioData, 0, audioData.size)
    }
}