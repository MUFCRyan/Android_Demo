package com.mufcryan.audio_video.api.wav

import com.mufcryan.audio_video.api.Util
import java.io.*

class WavFileWriter: Util {
    private var mFilePath = ""
    private var mDataSize = 0
    private var mDataOutputStream: DataOutputStream? = null

    fun openFile(filePath: String, sampleRateInHz: Int, channels: Int, bitsPerSample: Int): Boolean{
        if (mDataOutputStream != null)
            closeFile()

        mFilePath = filePath
        mDataSize = 0
        mDataOutputStream = DataOutputStream(FileOutputStream(filePath))
        return writeHeader(sampleRateInHz, bitsPerSample, channels)
    }

    /**
     * 写文件的音频部分的二进制数据
     */
    fun writeData(buffer: ByteArray, offset: Int, count: Int): Boolean{
        if (mDataOutputStream == null)
            return false

        try {
            mDataOutputStream!!.write(buffer, offset,count)
        } catch (e: Exception){
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 写文件头部内容
     */
    private fun writeHeader(sampleRateInHz: Int, bitsPerSample: Int, channels: Int): Boolean {
        if (mDataOutputStream == null)
            return false

        val header = WavFileHeader(sampleRateInHz, bitsPerSample, channels)

        try {
            mDataOutputStream!!.writeBytes(header.mChunkId)
            mDataOutputStream!!.write(intToByteArray(header.mChunkSize), 0, 4)
            mDataOutputStream!!.writeBytes(header.mFormat)
            mDataOutputStream!!.writeBytes(header.mSubChunk1ID)
            mDataOutputStream!!.write(intToByteArray(header.mSubChuck1Size), 0, 4)
            mDataOutputStream!!.write(shortToByteArray(header.mAudioFormat), 0, 2)
            mDataOutputStream!!.write(shortToByteArray(header.mNumChannel), 0, 2)
            mDataOutputStream!!.write(intToByteArray(header.mSampleRate), 0, 4)
            mDataOutputStream!!.write(intToByteArray(header.mByteRate), 0, 4)
            mDataOutputStream!!.write(shortToByteArray(header.mBlockAlign), 0, 2)
            mDataOutputStream!!.write(shortToByteArray(header.mBitsPerSample), 0, 2)
            mDataOutputStream!!.writeBytes(header.mSubChunk2ID)
            mDataOutputStream!!.write(intToByteArray(header.mSubChunk2Size), 0, 4)
        } catch (e: Exception){
            e.printStackTrace()
            return false
        }
        return true
    }

    fun closeFile(): Boolean {
        var ret = true
        if (mDataOutputStream != null){
            ret = writeDataSize()
            mDataOutputStream!!.close()
            mDataOutputStream = null
        }
        return ret
    }

    /**
     * 在写完数据后修改表示文件大小的字段
     */
    private fun writeDataSize(): Boolean {
        if (mDataOutputStream == null)
            return false

        try {
            val wavFile = RandomAccessFile(mFilePath, "rw")
            wavFile.seek(WavFileHeader.WAV_CHUNKSIZE_OFFSET.toLong())
            wavFile.write(intToByteArray(mDataSize + WavFileHeader.WAV_CHUNK_SIZE_EXCLUDE_DATA))
            wavFile.seek(WavFileHeader.WAV_SUB_CHUNKSIZE2_OFFSET.toLong())
            wavFile.write(intToByteArray(mDataSize), 0, 4)
            wavFile.close()
        } catch (e: FileNotFoundException){
            e.printStackTrace()
            return false
        } catch (e: IOException){
            e.printStackTrace()
            return false
        }

        return true
    }
}