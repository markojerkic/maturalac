package com.markojerkic.drzavnamatura

import java.io.Serializable

class QuestionImages(private val questions: ArrayList<Question>): Serializable {
    var questionsImagesDownloaded = 0
    var totalImages = totalImages()

    private fun totalImages(): Int {
        var total = 0
        for (question in questions) {
            if (question.imgURI != null)
                total++
            if (question.ansImg != null)
                total++
            if (question.checkSuperImage())
                total++
        }
        return total
    }

    fun checkQuestions(imagesProcessedCallback: QuestionImagesProcessedCallback) {
        // As Images are downloaded add them to the map
        for (question in questions) {
          question.checkImageDownload(object: ImageDownloadCallback {
              @Override
              override fun positiveCallBack() {
                  questionsImagesDownloaded++
                  imagesProcessedCallback.updateDownload(questionsImagesDownloaded.toDouble()/totalImages.toDouble())
                  // If number of images processed equal to number of questions sent
                  // Mark as done
                  if (questionsImagesDownloaded == totalImages)
                      imagesProcessedCallback.done()
              }
          })
        }
    }
}