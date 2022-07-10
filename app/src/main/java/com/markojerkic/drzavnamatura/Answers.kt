package com.markojerkic.drzavnamatura

import com.markojerkic.drzavnamatura.model.Question

class Answers {
    private val abcdAnswers = HashMap<Question, Int>()
    private val typeAnswers = HashMap<Question, String>()

    // Add abcd answer
    fun add(question: Question, ans: Int) {
        if (question.typeOfAnswer == AnswerType.ABCD.ordinal)
            abcdAnswers[question] = ans
    }

    // Add typed answer
    fun add(question: Question, ans: String) {
        if (question.typeOfAnswer == AnswerType.TYPE.ordinal)
            typeAnswers[question] = ans
    }

    // Check if answer is already given
    fun containsAnswer(question: Question): Boolean {
        return abcdAnswers.containsKey(question) || typeAnswers.containsKey(question)
    }

    // Get answer as Any?, which will have to be casted
    fun getAns(question: Question): Any? {
        if (question.typeOfAnswer == AnswerType.ABCD.ordinal)
            return abcdAnswers[question]
        else if (question.typeOfAnswer == AnswerType.TYPE.ordinal)
            return typeAnswers[question]
        return null
    }

}