package com.markojerkic.drzavnamatura

import java.io.Serializable

public class Question (private val questionMap: Map<String, Any>): Serializable {
    val question = questionMap["question"].toString()
    val ansA = questionMap["ansA"].toString()
    val ansB = questionMap["ansB"].toString()
    val ansC = questionMap["ansC"].toString()
    val ansD = questionMap["ansD"].toString()
    val correctAns: Long = questionMap["correctAns"] as Long
    val typeOfAnswer: AnswerType = findAnswerType(questionMap["typeOfAnswer"] as Long)
    val subject = questionMap["subject"].toString()
    val year = questionMap["year"].toString()
    var givenAns = String()

    private fun findAnswerType(at: Long): AnswerType {
        if (at == 0.toLong())
            return AnswerType.ABCD
        return AnswerType.TYPE
    }
}