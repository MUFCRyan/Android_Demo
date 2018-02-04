package com.mufcryan.audio_video.api.audio

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import java.io.IOException

class AudioDecoder {
    companion object {
        private val TAG = AudioDecoder.javaClass.simpleName
        private val DEFAULT_MIME_TYPE = "audio/mp4a-latm"
        private val DEFAULT_CHANNEL_NUM = 1
        private val DEFAULT_SAMPLE_RATE = 44100
        private val DEFAULT_MAX_BUFFER_SIZE = 16348
    }

    interface OnAudioDecodedListener {
        fun onFrameDecoded(decoded: ByteArray, presentationTimeUs: Long)
    }

    private var mMedicCodec: MediaCodec? = null
    private var mAudioDecodedListener: OnAudioDecodedListener? = null
    private var mIsOpened = false
    private var mIsFirstFrame = true

    fun open(): Boolean{
        if (mIsOpened)
            return true
        return open(DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_NUM, DEFAULT_MAX_BUFFER_SIZE)
    }

    private fun open(sampleRate: Int, channels: Int, maxBufferSize: Int): Boolean{
        Log.i(TAG, "open audio decoder: $sampleRate, $channels, $maxBufferSize")
        if (mIsOpened)
            return true

        try {
            mMedicCodec = MediaCodec.createDecoderByType(DEFAULT_MIME_TYPE)
            val format = MediaFormat()
            format.setString(MediaFormat.KEY_MIME, DEFAULT_MIME_TYPE)
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, DEFAULT_CHANNEL_NUM)
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, DEFAULT_SAMPLE_RATE)
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize)
            mMedicCodec!!.configure(format, null, null, 0)
            mMedicCodec!!.start()
            mIsOpened = true
        } catch (e: IOException){
            e.printStackTrace()
            return false
        }

        Log.i(TAG, "open audio decoder success!")
        return true
    }

    fun close(){
        Log.i(TAG, "close audio decoder +")
        if (!mIsOpened)
            return

        mMedicCodec!!.stop()
        mMedicCodec!!.release()
        mMedicCodec = null
        mIsOpened = false
        Log.i(TAG, "close audio decoder -")
    }

    fun isOpened(): Boolean{
        return mIsOpened
    }

    fun setAudioDecodedListener(listener: OnAudioDecodedListener){
        mAudioDecodedListener = listener
    }

    @Synchronized fun decode(input: ByteArray, presentationTimeUs: Long): Boolean{
        Log.d(TAG, "decode: $presentationTimeUs")
        if (!mIsOpened)
            return false

        try {
            val inputBuffers = mMedicCodec!!.inputBuffers
            val inputBufferIndex = mMedicCodec!!.dequeueInputBuffer(1000)
            if (inputBufferIndex >= 0){
                val inputBuffer = inputBuffers[inputBufferIndex]
                inputBuffer.clear()
                inputBuffer.put(input)
                if (mIsFirstFrame){
                    /**
                     * Some formats, notably AAC audio and MPEG4, H.265 video formats
                     * require the actual data to be prefixed by a number of buffers contains
                     * setup data, or codec specific data. When processing such compressed formats
                     * this data must be submitted to the codec after start() and before any
                     * frame data. Such data must be marked using the flag
                     * BUFFER_FLAG_CODEC_CONFIG in a call to queueInputBuffer.
                     */
                    mMedicCodec!!.queueInputBuffer(inputBufferIndex, 0, input.size, presentationTimeUs, MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
                    mIsFirstFrame = false
                } else {
                    mMedicCodec !!.queueInputBuffer(inputBufferIndex, 0, input.size, presentationTimeUs, 0)
                }
            }
        } catch (t: Throwable){
            t.printStackTrace()
            return false
        }

        Log.d(TAG, "decode -")
        return false
    }

    @Synchronized fun retrieve(): Boolean{
        Log.d(TAG, "decode retrieve +")
        if (!mIsOpened)
            return false

        try {
            val outputBuffers = mMedicCodec!!.outputBuffers
            val bufferInfo = MediaCodec.BufferInfo()
            val outputBufferIndex = mMedicCodec!!.dequeueOutputBuffer(bufferInfo, 1000)
            if (outputBufferIndex >= 0){
                Log.d(TAG, "decode retrieve frame " + bufferInfo.size)
                val outputBuffer = outputBuffers[outputBufferIndex]
                val outData = ByteArray(bufferInfo.size)
                outputBuffer.get(outData)
                if (mAudioDecodedListener != null)
                    mAudioDecodedListener!!.onFrameDecoded(outData, bufferInfo.presentationTimeUs)
                mMedicCodec!!.releaseOutputBuffer(outputBufferIndex, false)
            }
        } catch (t: Throwable){
            t.printStackTrace()
            return false
        }

        Log.d(TAG, "decode retrieve -")
        return true
    }
}