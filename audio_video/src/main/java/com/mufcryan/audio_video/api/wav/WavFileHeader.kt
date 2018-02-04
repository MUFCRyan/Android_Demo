package com.mufcryan.audio_video.api.wav

/**
 * Created by zhaofengchun on 2018/1/29.
 *
 */
class WavFileHeader() {
    companion object {
        const val WAV_FILE_HEADER_SIZE = 44
        const val WAV_CHUNK_SIZE_EXCLUDE_DATA = 36
        const val WAV_CHUNKSIZE_OFFSET = 4
        const val WAV_SUB_CHUNKSIZE1_OFFSET = 16
        const val WAV_SUB_CHUNKSIZE2_OFFSET = 40
    }

    var mChunkId = "RIFF"
    var mChunkSize = 0
    var mFormat = "WAVE"

    var mSubChunk1ID = "fmt "
    var mSubChuck1Size = 0
    var mAudioFormat: Short = 1
    var mNumChannel: Short = 1
    var mSampleRate = 44100
    var mByteRate = 0
    var mBlockAlign: Short = 0
    var mBitsPerSample: Short = 8

    var mSubChunk2ID = "data"
    var mSubChunk2Size = 0

    constructor(sampleSizeInHz: Int, bitsPerSample: Int, channels: Int): this(){
        mSampleRate = sampleSizeInHz
        mBitsPerSample = bitsPerSample.toShort()
        mNumChannel = channels.toShort()
        mByteRate = mSampleRate * mNumChannel * mBitsPerSample / 8
        mBlockAlign = (mNumChannel * mBitsPerSample / 8).toShort()
    }
}