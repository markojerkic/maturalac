package com.markojerkic.drzavnamatura

import java.io.Serializable

class QuestionFiles(private val questions: ArrayList<Question>): Serializable {
    var filesDownloaded = 0
    var totalFiles = totalFiles()

    private fun totalFiles(): Int {
        var total = 0
        for (question in questions) {
            if (question.imgURI != null)
                total++
            if (question.ansImg != null)
                total++
            if (question.checkSuperImage())
                total++
            if (question.audioFileName() != null)
                total++
        }
        return total
    }

    fun checkQuestions(filesProcessedCallback: QuestionFilesProcessedCallback) {
        // As Images are downloaded add them to the map
        for (question in questions) {
          question.checkFileDownload(object: FileDownloadCallback {
              @Override
              override fun positiveCallBack() {
                  filesDownloaded++
                  filesProcessedCallback.updateDownload(filesDownloaded.toDouble()/totalFiles.toDouble())
                  // If number of images processed equal to number of questions sent
                  // Mark as done
                  if (filesDownloaded == totalFiles)
                      filesProcessedCallback.done()
              }
          })
        }
    }
}