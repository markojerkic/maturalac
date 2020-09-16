package com.markojerkic.drzavnamatura

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.Serializable

object ImagesSingleton: Serializable {
    private val images: HashMap<String, ByteArray> = HashMap()
    private val firebaseStorage = Firebase.storage.reference

    fun add(id: String, byteArray: ByteArray) {
        images[id] = byteArray
    }

    fun getByteArray(id: String): ByteArray {
        return images[id]!!
    }

    fun containsKey(key: String): Boolean {
        return images.containsKey(key)
    }

    fun downloadImg(question: Question, callback: ImageDownloadCallback) {
        val ONE_MEGABYTE: Long = 1024*1024
        firebaseStorage.child(question.imgURI!!).getBytes(ONE_MEGABYTE).addOnSuccessListener { ba ->
            Log.d("image", question.imgURI)
            Log.d("image", ba.contentToString())
            Log.d("image", "\n")
            images[question.id] = ba
            callback.positiveCallBack()
        }
            .addOnCanceledListener { callback.negativeCallBack() }
            .addOnFailureListener{callback.negativeCallBack()}
    }
}