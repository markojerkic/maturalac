package com.markojerkic.drzavnamatura

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.Serializable

object ImagesSingleton: Serializable {
    private val images: HashMap<String, ByteArray> = HashMap()
    private val answerImages: HashMap<String, ByteArray> = HashMap()
    private val superQuestionImages: HashMap<String, ByteArray> = HashMap()
    private val firebaseStorage = Firebase.storage.reference
    // For image download, upper limit
    val ONE_MEGABYTE: Long = 1024*1024

    fun add(id: String, byteArray: ByteArray) {
        images[id] = byteArray
    }

    fun getAnswerByteArray(id: String): ByteArray {
        return answerImages[id]!!
    }

    fun containsAnswerKey(key: String): Boolean {
        return answerImages.containsKey(key)
    }

    fun getByteArray(id: String): ByteArray {
        return images[id]!!
    }

    fun containsKey(key: String): Boolean {
        return images.containsKey(key)
    }

    fun printAns() {
        Log.d("ans images", answerImages.toString())
    }

    fun downloadAnsImg(question: Question, callback: ImageDownloadCallback) {
        // Download answer image if exists
        if (!answerImages.containsKey(question.id)) {
            firebaseStorage.child(question.ansImg!!).getBytes(ONE_MEGABYTE).addOnSuccessListener { ba ->
                answerImages[question.id] = ba
                callback.positiveCallBack()
            }.addOnFailureListener{callback.positiveCallBack()}.addOnCanceledListener { callback.positiveCallBack() }
        }

    }

    fun downloadImg(question: Question, callback: ImageDownloadCallback) {
        // If image is already stored in memory, don't download it
        // Just call the positive callback
        if (!images.containsKey(question.id)) {
            firebaseStorage.child(question.imgURI!!).getBytes(ONE_MEGABYTE)
                .addOnSuccessListener { ba ->
                    Log.d("image", question.imgURI)
                    Log.d("image", ba.contentToString())
                    Log.d("image", "\n")
                    images[question.id] = ba
                    callback.positiveCallBack()
                }
                .addOnCanceledListener { callback.positiveCallBack() }
                .addOnFailureListener { callback.positiveCallBack() }
        }
    }

    fun downloadSuperImg(superImageName: String, callback: ImageDownloadCallback) {
        if (superImageName != null && superImageName != "" && !superQuestionImages.containsKey(superImageName)) {
            firebaseStorage.child(superImageName).getBytes(ONE_MEGABYTE).addOnSuccessListener { ba ->
                superQuestionImages[superImageName] = ba
                callback.positiveCallBack()
            }.addOnFailureListener{callback.positiveCallBack()}.addOnCanceledListener { callback.positiveCallBack() }
        } else if (superQuestionImages.containsKey(superImageName)) {
            callback.positiveCallBack()
        }
    }

    fun containsSuperImage(superQuestionName: String): Boolean {
        return superQuestionImages.containsKey(superQuestionName)
    }

    fun getSuperByteArray(superQuestionName: String): ByteArray {
        return superQuestionImages[superQuestionName]!!
    }
}