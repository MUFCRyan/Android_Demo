package com.mufcryan.audio_video.api.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

/**
 * Created by zhaofengchun on 2018/1/28.
 *
 */
class AudioCapture {
    private lateinit var mAudioRecord: AudioRecord
    private var mMinBufferSize = 0
    private lateinit var mCaptureThread: Thread
    private var mIsCaptureStarted = false
    @Volatile private var mIsLoopExit = false

    private var mAudioFrameCapturedListener: OnAudioFrameCapturedListener? = null

    interface OnAudioFrameCapturedListener {
        fun onAudioFrameCaptured(audioData: ByteArray)
    }

    fun setOnAudioFrameCapturedListener(listener: OnAudioFrameCapturedListener){
        mAudioFrameCapturedListener = listener
    }

    fun isCaptureStarted(): Boolean{
        return mIsCaptureStarted
    }

    fun isLoopExit(): Boolean{
        return mIsLoopExit
    }

    fun startCapture(audioSource: Int = DEFAULT_SOURCE, sampleRateInHz: Int = DEFAULT_SAMPLE_RATE, channelConfig: Int = DEFAULT_CHANNEL_CONFIG, audioFormat: Int = DEFAULT_AUDIO_FORMAT): Boolean{
        if (mIsCaptureStarted){
            Log.e(TAG, "Already start capture audio data!")
            return false
        }

        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG, "Invalid parameters!")
            return false
        }
        Log.d(TAG, "minBufferSize = $mMinBufferSize bytes!")

        mAudioRecord = AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, mMinBufferSize)
        if (mAudioRecord.state == AudioRecord.STATE_UNINITIALIZED){
            Log.e(TAG, "AudioRecord initialize fail!")
            return false
        }

        mAudioRecord.startRecording()

        mIsLoopExit = false

        mCaptureThread = Thread(AudioCaptureRunnable())
        mCaptureThread.start()
        mIsCaptureStarted = true

        Log.d(TAG, "Start audio capture success!")
        return true
    }

    fun stopCapture(){
        if (!mIsCaptureStarted)
            return
        mIsLoopExit = true
        try {
            mCaptureThread.interrupt()
            mCaptureThread.join(1000)
        } catch (e: InterruptedException){
            e.printStackTrace()
            Log.e(TAG, "Get an exception in stopCapture: ${e.message}")
        }

        if (mAudioRecord.state == AudioRecord.RECORDSTATE_RECORDING)
            mAudioRecord.stop()
        mAudioRecord.release()

        mIsCaptureStarted = false
        mAudioFrameCapturedListener = null

        Log.d(TAG, "Stop audio capture success!")
    }

    inner class AudioCaptureRunnable: Runnable{
        override fun run() {
            while (!mIsLoopExit){
                val buffer = ByteArray(mMinBufferSize)
                val ret = mAudioRecord.read(buffer, 0, mMinBufferSize)
                when {
                    ret == AudioRecord.ERROR_INVALID_OPERATION -> Log.e(TAG, "Error ERROR_INVALID_OPERATION")
                    ret == AudioRecord.ERROR_BAD_VALUE -> Log.e(TAG, "Error ERROR_BAD_VALUE")
                    mAudioFrameCapturedListener != null -> mAudioFrameCapturedListener!!.onAudioFrameCaptured(buffer)
                }
            }
        }
    }

    companion object {
        private const val TAG = "AudioCapture"
        private const val DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC
        private const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
        private const val DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}