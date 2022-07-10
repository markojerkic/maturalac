package com.markojerkic.drzavnamatura.model

data class Error (
    var message: String,
    var code: String,
    var issues: List<Issue>
)