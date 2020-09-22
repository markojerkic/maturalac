package com.markojerkic.drzavnamatura

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
    val questionNumber = (questionMap["questionNumber"] as Long).toInt()
    val imgURI = checkForImage()
    val ansImg = checkForAnswerImage()
    var givenAns = String()

    fun checkImageDownload(callback: ImageDownloadCallback) {
        if (imgURI != null || ansImg!= null) {
            if (imgURI != null)
                downloadImg(callback)
            if (ansImg != null) {
                downloadAnsImg()
            }
            if (ansImg != null && imgURI == null)
                callback.negativeCallBack()
        } else {
            callback.negativeCallBack()
        }
        if (superQuestion() != null) {
            downloadSuperImage()
        }
    }

    private fun downloadAnsImg() {
        val imgSingleton = ImagesSingleton
        imgSingleton.downloadAnsImg(this)
    }

    private fun findAnswerType(at: Long): AnswerType {
        return when (at.toInt()) {
            0 -> AnswerType.ABCD
            1 -> AnswerType.TYPE
            2 -> AnswerType.LONG
            else -> AnswerType.LONG
        }
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

    private fun checkForAnswerImage(): String? {
        // If question map contains answer image uri, get it and return it
        // Also download the image
        if (questionMap.containsKey("ansImg")){
            return questionMap["ansImg"].toString() + ".png"
        }
        return null
    }

    fun superQuestion(): String? {
        if (questionMap.containsKey("superQuestion")) {
            if (questionMap["superQuestion"] != "")
                return questionMap["superQuestion"].toString()
        }
        return null
    }

    fun downloadSuperImage() {
        val imgSingleton = ImagesSingleton
        imgSingleton.downloadSuperImg(superImageName())
    }

    private fun downloadImg(callback: ImageDownloadCallback) {
        val imgSingleton = ImagesSingleton
        imgSingleton.downloadImg(this, callback)
    }

    fun superImageName(): String {
        return "super" + superQuestion()!!.split(" ").size+ superQuestion()!!.length + superQuestion()!!.split(" ")[0]

    }
}