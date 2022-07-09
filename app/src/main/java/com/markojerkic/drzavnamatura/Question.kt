package com.markojerkic.drzavnamatura

import java.io.Serializable

class Question(private val questionMap: Map<String, Any>, val id: String) : Serializable {
    val question = questionMap["question"].toString()
    val ansA = questionMap["ansA"].toString()
    val ansB = questionMap["ansB"].toString()
    val ansC = questionMap["ansC"].toString()
    val ansD = questionMap["ansD"].toString()
    val correctAns: Long = questionMap["correctAns"] as Long
    val typeOfAnswer: AnswerType = findAnswerType(questionMap["typeOfAnswer"] as Long)
    val questionNumber = (questionMap["questionNumber"] as Long).toInt()
    val imgURI = checkForImage()
    val ansImg = checkForAnswerImage()
    var givenAns = String()

    fun checkFileDownload(callback: FileDownloadCallback) {
        if (imgURI != null || ansImg != null || checkSuperImage() || audioFileName() != null) {
            if (FilesSingleton.containsKey(id))
                callback.positiveCallBack()
            else if (imgURI != null)
                downloadImg(callback)

            if (FilesSingleton.containsAnswerKey(id))
                callback.positiveCallBack()
            else if (ansImg != null) downloadAnsImg(callback)

            if (superQuestion() != null && checkSuperImage()) {
                if (FilesSingleton.containsSuperImage(superImageName()!!))
                    callback.positiveCallBack()
                else downloadSuperImage(callback)
            }

            if (FilesSingleton.containsAnswerKey(id))
                callback.positiveCallBack()
            else if (audioFileName() != null) downloadAudio(callback)

            if (ansImg != null && imgURI == null)
                callback.positiveCallBack()
        }
    }

    private fun downloadAudio(callback: FileDownloadCallback) {
        FilesSingleton.downloadAudio(this, callback)
    }

    private fun downloadAnsImg(callback: FileDownloadCallback) {
        val imgSingleton = FilesSingleton
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
        if (questionMap.containsKey("imageURI")) {
            return questionMap["imageURI"].toString() + ".png"
        }
        return null
    }

    private fun checkForAnswerImage(): String? {
        // If question map contains answer image uri, get it and return it
        // Also download the image
        if (questionMap.containsKey("ansImg")) {
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

    private fun downloadSuperImage(callback: FileDownloadCallback) {
        val imgSingleton = FilesSingleton
        imgSingleton.downloadSuperImg(superImageName()!!, callback)
    }

    private fun downloadImg(callback: FileDownloadCallback) {
        val imgSingleton = FilesSingleton
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

    // Check if audio file exists
    fun audioFileName(): String? {
        if (questionMap.containsKey("audioName"))
            return questionMap["audioName"].toString() + ".mp3"
        return null
    }
}