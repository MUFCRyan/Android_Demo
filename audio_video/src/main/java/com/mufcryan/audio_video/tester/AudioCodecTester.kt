package com.mufcryan.audio_video.tester

import com.mufcryan.audio_video.api.audio.AudioCapture
import com.mufcryan.audio_video.api.audio.AudioDecoder
import com.mufcryan.audio_video.api.audio.AudioEcoder
import com.mufcryan.audio_video.api.audio.AudioPlayer

class AudioCodecTester: Tester(), AudioCapture.OnAudioFrameCapturedListener, AudioEcoder.OnAudioEncodedListener, AudioDecoder.OnAudioDecodedListener {
    private var mAudioEncoder: AudioEcoder? = null
    private var mAudioDecoder: AudioDecoder? = null
    private var mAudioCapture: AudioCapture? = null
    private var mAudioPlayer: AudioPlayer? = null
    @Volatile private var mIsTestingExit = false

    override fun startTesting(): Boolean {
        mAudioCapture = AudioCapture()
        mAudioPlayer = AudioPlayer()
        mAudioEncoder = AudioEcoder()
        mAudioDecoder = AudioDecoder()
        if (!mAudioEncoder!!.open() || !mAudioDecoder!!.open())
            return false
        mAudioEncoder!!.setAudioEncodedListener(this)
        mAudioDecoder!!.setAudioDecodedListener(this)
        mAudioCapture!!.setOnAudioFrameCapturedListener(this)
        Thread {mEncodeRenderRunnable}.start()
        Thread {mDecodeRenderRunnable}.start()
        if (!mAudioCapture!!.startCapture()){
            return false
        }
        mAudioPlayer!!.startPlayer()
        return true
    }

    override fun stopTesting(): Boolean {
        mIsTestingExit = true
        mAudioCapture!!.stopCapture()
        return true
    }

    override fun onAudioFrameCaptured(audioData: ByteArray) {
        val presentationTimeUs = (System.nanoTime()) / 1000L
        mAudioEncoder!!.encode(audioData, presentationTimeUs)
    }

    override fun onFrameEncoded(encoded: ByteArray, presentationTimeUs: Long) {
        mAudioDecoder!!.decode(encoded, presentationTimeUs)
    }

    override fun onFrameDecoded(decoded: ByteArray, presentationTimeUs: Long) {
        mAudioPlayer!!.play(decoded, 0, decoded.size)
    }

    private val mEncodeRenderRunnable = {
        while (!mIsTestingExit)
            mAudioEncoder!!.retrieve()
        mAudioEncoder!!.close()
    }

    private val mDecodeRenderRunnable = {
        while (!mIsTestingExit)
            mAudioDecoder!!.retrieve()
        mAudioDecoder!!.close()
    }
}