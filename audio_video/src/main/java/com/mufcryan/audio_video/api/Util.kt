package com.mufcryan.audio_video.api

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Created by zhaofengchun on 2018/1/29.
 *
 */
interface Util {
    fun intToByteArray(data: Int): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array()
    }

    fun byteArrayToInt(data: ByteArray): Int {
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).int
    }

    fun shortToByteArray(data: Short): ByteArray {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array()
    }

    fun byteArrayToShort(data: ByteArray): Short {
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).short
    }
}