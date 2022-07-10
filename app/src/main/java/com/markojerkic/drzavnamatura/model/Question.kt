package com.markojerkic.drzavnamatura.model

data class Question (
    var question: String,
    var superQuestion: String?,
    var ansA: String,
    var ansB: String,
    var ansC: String,
    var ansD: String,
    var typeOfAnswer: Int,
    var questionNumber: Int,
    var subject: String,
    var year: String,
    var superQuestionImage: String?,
    var imageURI: String?,
    var imageDownloadUrl: String?,
    var superQuestionImageDownloadUrl: String?,
    var audioDownloadUrl: String?,
    var correctAnswer: Int?
)