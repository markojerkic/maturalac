package com.markojerkic.drzavnamatura

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.Serializable

class Question (private val questionMap: Map<String, Any>, val id: String): Serializable {
    val question = questionMap["question"].toString()
    val ansA = questionMap["ansA"].toString()
    val ansB = questionMap["ansB"].toString()
    val ansC = questionMap["ansC"].toString()
    val ansD = questionMap["ansD"].toString()
    val correctAns: Long = questionMap["correctAns"] as Long
    val typeOfAnswer: AnswerType = findAnswerType(questionMap["typeOfAnswer"] as Long)
    val subject = questionMap["subject"].toString()
    val year = questionMap["year"].toString()
    val imgURI = checkForImage()
    var givenAns = String()

    fun checkImageDownload(callback: ImageDownloadCallback) {
        if(imgURI != null && imgURI.split(" ").size < 2) {
            downloadImg(callback)
        } else {
            callback.negativeCallBack()
        }
    }

    private fun findAnswerType(at: Long): AnswerType {
        if (at == 0.toLong())
            return AnswerType.ABCD
        return AnswerType.TYPE
    }

    private fun createImageName(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(this.question.split(" ").size as String)
        stringBuilder.append(this.question.split(""[0]))
        stringBuilder.append(this.question)
        stringBuilder.append(this.correctAns)
        return stringBuilder.toString()
    }

    private fun checkForImage(): String? {
        // If question map contains image uri, get it and return it
        // Also download the image
        if (questionMap.containsKey("imageURI")){
            return questionMap["imageURI"].toString() + ".png"
        }
        return null
    }

    private fun downloadImg(callback: ImageDownloadCallback) {
        val ONE_MEGABYTE: Long = 1024*1024
        Firebase.storage.reference.child(this.imgURI!!).getBytes(ONE_MEGABYTE).addOnSuccessListener { ba ->
            Log.d("image", ba.contentToString())
            callback.positiveCallBack(ba)
        }
            .addOnCanceledListener { callback.negativeCallBack() }
            .addOnFailureListener{callback.negativeCallBack()}
    }
}