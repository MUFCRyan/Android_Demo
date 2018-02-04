package com.mufcryan.audio_video.api.wav

import android.util.Log
import com.mufcryan.audio_video.api.Util
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.IOException

class WavFileReader: Util {
    companion object {
        val TAG = WavFileReader.javaClass.simpleName
    }

    private var mDataInputStream: DataInputStream? = null
    private var mWavFileHeader: WavFileHeader? = null

    fun openFile(filePath: String): Boolean {
        if (mDataInputStream != null) {
            closeFile()
        }
        mDataInputStream = DataInputStream(FileInputStream(filePath))
        return readHeader()
    }

    private fun closeFile() {
        if (mDataInputStream != null) {
            mDataInputStream!!.close()
            mDataInputStream = null
        }
    }

    /**
     * 读取文件音频部分二进制数据
     */
    fun readData(buffer: ByteArray, offset: Int, count: Int): Int{
        if (mDataInputStream == null || mWavFileHeader == null)
            return  -1

        try {
            val nBytes = mDataInputStream!!.read(buffer, offset, count)
            if (nBytes == -1){
                return 0
            }
            return nBytes
        } catch (e: IOException){
            e.printStackTrace()
        }

        return -1
    }

    fun getWavFileHeader(): WavFileHeader?{
        return mWavFileHeader
    }

    /**
     * 逐行读取文件头部内容
     */
    private fun readHeader(): Boolean {
        if (mDataInputStream == null)
            return false

        val header = WavFileHeader()

        val intValue = ByteArray(4)
        val shortValue = ByteArray(4)

        try {
            mDataInputStream?.run {
                header.mChunkId = "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!
                        .readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}"
                Log.d(TAG, "Read chunkID: " + header.mChunkId)

                mDataInputStream!!.read(intValue)
                header.mChunkSize = byteArrayToInt(intValue)
                Log.d(TAG, "Read file chunkSize: " + header.mChunkSize)

                header.mFormat = "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}"
                Log.d(TAG, "Read file format: " + header.mFormat)

                header.mSubChunk1ID = "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}"
                Log.d(TAG, "Read fmt chunkID: " + header.mSubChunk1ID)

                mDataInputStream!!.read(intValue)
                header.mSubChuck1Size = byteArrayToInt(intValue)
                Log.d(TAG, "Read fmt chunkSize: " + header.mSubChuck1Size)

                mDataInputStream!!.read(shortValue)
                header.mAudioFormat = byteArrayToShort(shortValue)
                Log.d(TAG, "Read audioFormat: " + header.mAudioFormat)

                mDataInputStream!!.read(shortValue)
                header.mNumChannel = byteArrayToShort(shortValue)
                Log.d(TAG, "Read channel number: " + header.mNumChannel)

                mDataInputStream!!.read(intValue)
                header.mSampleRate = byteArrayToInt(intValue)
                Log.d(TAG, "Read sampleRate: " + header.mSampleRate)

                mDataInputStream!!.read(intValue)
                header.mByteRate = byteArrayToInt(intValue)
                Log.d(TAG, "Read byteRate: " + header.mByteRate)

                mDataInputStream!!.read(shortValue)
                header.mBlockAlign = byteArrayToShort(shortValue)
                Log.d(TAG, "Read blockAlign: " + header.mBlockAlign)

                mDataInputStream!!.read(shortValue)
                header.mBitsPerSample = byteArrayToShort(shortValue)
                Log.d(TAG, "Read bitsPerSample: " + header.mBitsPerSample)

                header.mSubChunk2ID = "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}" + "${(mDataInputStream!!.readByte()).toChar()}"
                Log.d(TAG, "Read data chunkID: " + header.mSubChunk2ID)

                mDataInputStream!!.read(intValue)
                header.mSubChunk2Size = byteArrayToInt(intValue)
                Log.d(TAG, "Read data chunkSize: " + header.mSubChunk2Size)

                Log.d(TAG, "Read wav file success!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        mWavFileHeader = header
        return true
    }
}