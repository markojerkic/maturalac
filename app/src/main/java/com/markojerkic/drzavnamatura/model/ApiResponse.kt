package com.markojerkic.drzavnamatura.model

data class ApiResponse<T> (
    var ok: Boolean,
    var data: List<T>?,
    var error: Error?
)