package com.markojerkic.drzavnamatura.service

import com.markojerkic.drzavnamatura.model.ApiResponse
import com.markojerkic.drzavnamatura.model.Question
import com.markojerkic.drzavnamatura.model.Subject
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("exams")
    fun getPublicExams(): Observable<ApiResponse<Subject>>

    @GET("questions")
    fun getQuestionsBySubjectAndYear(@Query("subject") subject: String,
                                     @Query("exam") year: String): Observable<ApiResponse<Question>>
}