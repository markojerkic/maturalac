package com.markojerkic.drzavnamatura

object QuestionsObject {
    private val questionsMap = HashMap<String, ArrayList<Question>>()

    fun addQuestions(exam: String, questions: ArrayList<Question>) {
        questionsMap[exam] = questions
    }

    fun getQuestions(exam: String): ArrayList<Question> {
        return questionsMap[exam]!!
    }
}