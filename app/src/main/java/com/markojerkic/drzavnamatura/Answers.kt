package com.markojerkic.drzavnamatura

import com.markojerkic.drzavnamatura.model.Question

class Answers {
    private val abcdAnswers = HashMap<String, Int>()
    private val typeAnswers = HashMap<String, String>()

    // Add abcd answer
    fun add(question: Question, ans: Int) {
        if (question.typeOfAnswer == AnswerType.ABCD.ordinal)
            abcdAnswers[question.id] = ans
    }

    // Add typed answer
    fun add(question: Question, ans: String) {
        if (question.typeOfAnswer == AnswerType.TYPE.ordinal)
            typeAnswers[question.id] = ans
    }

    // Check if answer is already given
    fun containsAnswer(question: Question): Boolean {
        return abcdAnswers.containsKey(question.id) || typeAnswers.containsKey(question.id)
    }

    // Get answer as Any?, which will have to be casted
    fun getAns(question: Question): Any? {
        if (question.typeOfAnswer == AnswerType.ABCD.ordinal)
            return abcdAnswers[question.id]
        else if (question.typeOfAnswer == AnswerType.TYPE.ordinal)
            return typeAnswers[question.id]
        return null
    }

}