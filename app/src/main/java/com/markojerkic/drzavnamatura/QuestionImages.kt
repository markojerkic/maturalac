package com.markojerkic.drzavnamatura

import java.io.Serializable

class QuestionImages(private val questions: ArrayList<Question>): Serializable {
    var questionsImagesDownloaded = 0

    fun checkQuestions(imagesProcessedCallback: QuestionImagesProcessedCallback) {
        // Get instance of ImageSingleton
        val imageSingleton = ImagesSingleton
        // As Images are downloaded add them to the map
        for (question in questions) {
          question.checkImageDownload(object: ImageDownloadCallback {
              @Override
              override fun positiveCallBack() {
                  questionsImagesDownloaded++
                  // If number of images processed equal to number of questions sent
                  // Mark as done
                  if (questionsImagesDownloaded == questions.size-1)
                      imagesProcessedCallback.done()
              }

              @Override
              override fun negativeCallBack() {
                  questionsImagesDownloaded++
                  // If number of images processed equal to number of questions sent
                  // Mark as done<
                  if (questionsImagesDownloaded == questions.size-1)
                      imagesProcessedCallback.done()
              }
          })
        }
    }
}