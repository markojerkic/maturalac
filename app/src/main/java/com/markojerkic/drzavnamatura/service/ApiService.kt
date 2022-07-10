package com.markojerkic.drzavnamatura.service

import com.markojerkic.drzavnamatura.model.ApiResponse
import com.markojerkic.drzavnamatura.model.Question
import com.markojerkic.drzavnamatura.model.Subject
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("exams")
    fun getPublicExams(): Observable<ApiResponse<Subject>>

    @GET("questions")
    fun getQuestionsBySubjectAndYear(@Path("subject") subject: String,
                                     @Path("year") year: String): Observable<ApiResponse<List<Question>>>
}