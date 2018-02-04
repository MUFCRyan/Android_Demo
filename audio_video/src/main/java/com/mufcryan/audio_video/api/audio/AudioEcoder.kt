package com.mufcryan.audio_video.api.audio

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import java.io.IOException

class AudioEcoder {
    companion object {
        private val TAG = AudioEcoder.javaClass.simpleName
        private val DEFAULT_MIME_TYPE = "audio/mp4a-latm"
        private val DEFAULT_CHANNEL_NUM = 1
        private val DEFAULT_SAMPLE_RATE = 44100
        private val DEFAULT_BITRATE = 128 * 1000 //AAC-LC, 64 * 1024 for AAC-HE
        private val DEFAULT_PROFILE_LEVEL = MediaCodecInfo.CodecProfileLevel.AACObjectLC
        private val DEFALUT_MAX_BUFFER_SIZE = 16348
    }

    interface OnAudioEncodedListener {
        fun onFrameEncoded(encoded: ByteArray, presentationTimeUs: Long)
    }

    private var mMediaCodec: MediaCodec? = null
    private var mAudioEncodedListener: OnAudioEncodedListener? = null
    private var mIsOpened = false

    fun open(): Boolean{
        if (mIsOpened)
            return true
        return open(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_NUM, DEFAULT_BITRATE, DEFALUT_MAX_BUFFER_SIZE)
    }

    private fun open(sampleRate: Int, channels: Int, bitRate: Int, maxBufferSize: Int): Boolean{
        Log.i(TAG, "open audio encoder: $sampleRate, $channels, $maxBufferSize")
        if (mIsOpened)
            return true

        try {
            mMediaCodec = MediaCodec.createEncoderByType(DEFAULT_MIME_TYPE)
            val format = MediaFormat()
            format.setString(MediaFormat.KEY_MIME, DEFAULT_MIME_TYPE)
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channels)
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate)
            format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, DEFAULT_PROFILE_LEVEL)
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize)
            mMediaCodec!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mMediaCodec!!.start()
            mIsOpened = true
        } catch (e: IOException){
            e.printStackTrace()
            return false
        }
        Log.i(TAG, "open audio encoder success!")
        return true
    }

    fun close(){
        Log.i(TAG, "close audio encoder +")
        if (!mIsOpened)
            return
        mMediaCodec!!.stop()
        mMediaCodec!!.release()
        mMediaCodec = null
        mIsOpened = false
        Log.i(TAG, "close audio encoder -")
    }

    fun isOpened(): Boolean{
        return mIsOpened
    }

    fun setAudioEncodedListener(listener: OnAudioEncodedListener){
        mAudioEncodedListener = listener
    }

    @Synchronized fun encode(input: ByteArray, presentationTimeUs: Long): Boolean{
        Log.d(TAG, "encode: " + presentationTimeUs)
        if (!mIsOpened)
            return false

        try {
            val inputBuffers = mMediaCodec!!.inputBuffers
            val inputBufferIndex = mMediaCodec!!.dequeueInputBuffer(1000)
            if (inputBufferIndex >= 0){
                val inputBuffer = inputBuffers[inputBufferIndex]
                inputBuffer.clear()
                inputBuffer.put(input)
                mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, input.size, presentationTimeUs, 0)
            }
        } catch (t: Throwable){
            t.printStackTrace()
            return false
        }

        Log.d(TAG, "encode -")
        return true
    }

    @Synchronized fun retrieve(): Boolean{
        Log.d(TAG, "encode retrieve +")
        if (!mIsOpened)
            return false

        try {
            val outputBuffers = mMediaCodec!!.outputBuffers
            val bufferInfo = MediaCodec.BufferInfo()
            val outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(bufferInfo, 1000)
            if (outputBufferIndex >= 0){
                Log.d(TAG, "encode retrieve frame ${bufferInfo.size}")
                val outputBuffer = outputBuffers[outputBufferIndex]
                outputBuffer.position(bufferInfo.offset)
                outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                val frame = ByteArray(bufferInfo.size)
                outputBuffer.get(frame, 0, bufferInfo.size)
                if (mAudioEncodedListener != null)
                    mAudioEncodedListener!!.onFrameEncoded(frame, bufferInfo.presentationTimeUs)
                mMediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
            }
        } catch (t: Throwable){
            t.printStackTrace()
            return false
        }

        Log.d(TAG, "encode retrieve -")
        return true
    }
}
