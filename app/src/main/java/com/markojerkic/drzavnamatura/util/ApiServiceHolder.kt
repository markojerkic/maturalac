package com.markojerkic.drzavnamatura.util

import com.markojerkic.drzavnamatura.model.ApiResponse
import com.markojerkic.drzavnamatura.model.Question
import com.markojerkic.drzavnamatura.model.Subject
import com.markojerkic.drzavnamatura.service.ApiService
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceHolder {
    private var retrofit = Retrofit.Builder()
        .baseUrl("https://maturalac-markojerkic.vercel.app/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
    private val service = retrofit.create(ApiService::class.java)

    fun getPublicExamsTree(): Observable<ApiResponse<Subject>> = service.getPublicExams()

    fun getQuestionsBySubjectAndExam(subject: String, exam: String):
            Observable<ApiResponse<Question>> = service.getQuestionsBySubjectAndYear(subject, exam)
}