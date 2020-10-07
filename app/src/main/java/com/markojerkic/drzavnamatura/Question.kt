package com.markojerkic.drzavnamatura

import java.io.Serializable

class Question (private val questionMap: Map<String, Any>, val id: String): Serializable {
    val question = questionMap["question"].toString().replace("−", "-")
    val ansA = questionMap["ansA"].toString().replace("−", "-")
    val ansB = questionMap["ansB"].toString().replace("−", "-")
    val ansC = questionMap["ansC"].toString().replace("−", "-")
    val ansD = questionMap["ansD"].toString().replace("−", "-")
    val correctAns: Long = questionMap["correctAns"] as Long
    val typeOfAnswer: AnswerType = findAnswerType(questionMap["typeOfAnswer"] as Long)
    val questionNumber = (questionMap["questionNumber"] as Long).toInt()
    val imgURI = checkForImage()
    val ansImg = checkForAnswerImage()
    var givenAns = String()

    fun checkImageDownload(callback: ImageDownloadCallback) {
        if (imgURI != null || ansImg!= null || checkSuperImage()) {
            if (ImagesSingleton.containsKey(id))
                callback.positiveCallBack()
            else if (imgURI != null)
                downloadImg(callback)

            if (ImagesSingleton.containsAnswerKey(id))
                callback.positiveCallBack()
            else if (ansImg != null) downloadAnsImg(callback)

            if (superQuestion() != null && checkSuperImage()) {
                if (ImagesSingleton.containsSuperImage(superImageName()!!))
                    callback.positiveCallBack()
                else downloadSuperImage(callback)
            }
            if (ansImg != null && imgURI == null)
                callback.positiveCallBack()
        }
    }

    private fun downloadAnsImg(callback: ImageDownloadCallback) {
        val imgSingleton = ImagesSingleton
        imgSingleton.downloadAnsImg(this, callback)
    }

    private fun findAnswerType(at: Long): AnswerType {
        return when (at.toInt()) {
            0 -> AnswerType.ABCD
            1 -> AnswerType.TYPE
            2 -> AnswerType.LONG
            else -> AnswerType.LONG
        }
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
                return questionMap["superQuestion"].toString().replace("−", "-")
        }
        return null
    }

    private fun downloadSuperImage(callback: ImageDownloadCallback) {
        val imgSingleton = ImagesSingleton
        imgSingleton.downloadSuperImg(superImageName()!!, callback)
    }

    private fun downloadImg(callback: ImageDownloadCallback) {
        val imgSingleton = ImagesSingleton
        imgSingleton.downloadImg(this, callback)
    }

    fun superImgExists(): Boolean {
        return questionMap.containsKey("superQuestionImage")
    }

    fun superImageName(): String? {
        if (checkSuperImage())
            return questionMap["superQuestionImage"]!!.toString() + ".png"
        return null
    }
    fun checkSuperImage(): Boolean {
        return questionMap.containsKey("superQuestionImage")
    }
}