package com.markojerkic.drzavnamatura

import android.util.Log
import com.google.firebase.storage.StorageReference
import java.io.Serializable

class Question (private val questionMap: Map<String, Any>, val id: String, val storage: StorageReference): Serializable {
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
    lateinit var imgArray: ByteArray

    init {
        if(imgURI != null && imgURI.split(" ").size < 2) {
            downloadImg()
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

    private fun downloadImg() {
        val ONE_MEGABYTE: Long = 1024*1024
        storage.child(this.imgURI!!).getBytes(ONE_MEGABYTE).addOnSuccessListener { ba ->
            Log.d("image", ba.contentToString())
            imgArray = ba
        }
    }
}