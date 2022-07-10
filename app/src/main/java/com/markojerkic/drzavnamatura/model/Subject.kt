package com.markojerkic.drzavnamatura.model

data class Subject(
    var id: String?,
    var subject: String,
    var exams: List<String>
)