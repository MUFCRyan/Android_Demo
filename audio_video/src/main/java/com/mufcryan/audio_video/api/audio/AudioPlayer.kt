package com.mufcryan.audio_video.api.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log

/**
 * Created by zhaofengchun on 2018/1/29.
 *
 */
class AudioPlayer {
    companion object {
        private const val TAG = "AudioPlayer"
        private const val DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC
        private const val DEFAULT_SAMPLE_RATE = 44100
        private const val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
        private const val DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM
    }

    private var mIsPlayStarted = false
    private var mMinBufferSize = 0
    private lateinit var mAudioTrack: AudioTrack

    fun startPlayer(streamType: Int = DEFAULT_STREAM_TYPE, sampleRateSize: Int = DEFAULT_SAMPLE_RATE, channelConfig: Int = DEFAULT_CHANNEL_CONFIG, audioFormat: Int = DEFAULT_AUDIO_FORMAT, mode: Int = DEFAULT_PLAY_MODE): Boolean{
        if (mIsPlayStarted){
            Log.e(TAG, "Audio player already started!")
            return false
        }

        mMinBufferSize = AudioTrack.getMinBufferSize(sampleRateSize, channelConfig, audioFormat)
        if (mMinBufferSize == AudioTrack.ERROR_BAD_VALUE){
            Log.e(TAG, "Invalid parameters!")
            return false
        }
        Log.d(TAG, "getMinBufferSize = " + mMinBufferSize)

        mAudioTrack = AudioTrack(streamType, sampleRateSize, channelConfig, audioFormat, mMinBufferSize, mode)
        if(mAudioTrack.state == AudioTrack.STATE_UNINITIALIZED){
            Log.e(TAG, "AudioTrack initialized fail!")
            return false
        }

        mIsPlayStarted = true

        Log.d(TAG, "Start audio player success!")
        return true
    }

    fun getMinBufferSize(): Int{
        return mMinBufferSize
    }

    fun stopPlayer(){
        if (!mIsPlayStarted)
            return

        if (mAudioTrack.state == AudioTrack.PLAYSTATE_PLAYING)
            mAudioTrack.stop()

        mIsPlayStarted = false

        Log.d(TAG, "Stop audio player success!")
    }

    fun play(audioData: ByteArray, offsetInBytes: Int, sizeInBytes: Int): Boolean{
        if (!mIsPlayStarted){
            Log.e(TAG, "Player not started!")
            return false
        }

        if (sizeInBytes < mMinBufferSize){
            Log.e(TAG, "Audio data is not enough!")
            return false
        }

        if (mAudioTrack.write(audioData, offsetInBytes, sizeInBytes) != sizeInBytes){
            Log.e(TAG, "Could not write all the samples to the audio devices!")
        }

        mAudioTrack.play()

        Log.d(TAG, "Play audio player success!")

        return true
    }

}