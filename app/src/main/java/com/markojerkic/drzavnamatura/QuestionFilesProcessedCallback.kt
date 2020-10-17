package com.markojerkic.drzavnamatura

interface QuestionFilesProcessedCallback {
    // This will be called when all images from selected questions have been processed
    fun done()

    fun updateDownload(percent: Double)
}