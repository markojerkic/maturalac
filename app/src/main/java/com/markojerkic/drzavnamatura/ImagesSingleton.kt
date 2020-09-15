package com.markojerkic.drzavnamatura

import java.io.Serializable

object ImagesSingleton: Serializable {
    private val images: HashMap<String, ByteArray> = HashMap()

    fun add(id: String, byteArray: ByteArray) {
        images[id] = byteArray
    }

    fun getByteArray(id: String): ByteArray {
        return images[id]!!
    }

    fun containsKey(key: String): Boolean {
        return images.containsKey(key)
    }
}