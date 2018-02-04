package com.mufcryan.audio_video.tester

import com.mufcryan.audio_video.api.audio.AudioPlayer
import com.mufcryan.audio_video.api.wav.WavFileReader
import java.io.File
import java.io.IOException

class AudioPlayerTester: Tester() {
    companion object {
        private val SAMPLE_PER_FRAME= 1024
    }

    private lateinit var mAudioPlayer: AudioPlayer
    private lateinit var mWavFileReader: WavFileReader
    @Volatile private var mIstestingExit = false

    override fun startTesting(): Boolean {
        mWavFileReader = WavFileReader()
        mAudioPlayer = AudioPlayer()
        try {
            mWavFileReader.openFile(File(DEFAULT_TEST_DIR, DEFAULT_TEST_FILE).path)
        } catch (e: IOException){
            e.printStackTrace()
            return false
        }
        mAudioPlayer.startPlayer()
        Thread(mAudioPlayerRunnable).start()
        return true
    }

    override fun stopTesting(): Boolean {
        mIstestingExit = true
        return true
    }

    private val mAudioPlayerRunnable = Runnable {
        val buffer = ByteArray(SAMPLE_PER_FRAME * 2)
        while (!mIstestingExit && mWavFileReader.readData(buffer, 0, buffer.size) > 0){
            mAudioPlayer.play(buffer, 0, buffer.size)
        }
        mAudioPlayer.stopPlayer()
        try {
            mAudioPlayer.stopPlayer()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}