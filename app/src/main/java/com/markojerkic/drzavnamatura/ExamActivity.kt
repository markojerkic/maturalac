package com.markojerkic.drzavnamatura

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ExamActivity : AppCompatActivity() {

    // Set up counter for the questions
    var counter = 0

    // Find elements
    private val questionsTextView by lazy { findViewById<TextView>(R.id.question_text_view) }
    private val questionImageView by lazy { findViewById<ImageView>(R.id.question_image) }
    private val ansAView by lazy { findViewById<TextView>(R.id.answer_a) }
    private val ansBView by lazy { findViewById<TextView>(R.id.answer_b) }
    private val ansCView by lazy { findViewById<TextView>(R.id.answer_c) }
    private val ansDView by lazy { findViewById<TextView>(R.id.answer_d) }
    // Find elements for switching questions
    private val nextQuestion by lazy { findViewById<ImageView>(R.id.next_question) }
    private val previousQuestion by lazy { findViewById<ImageView>(R.id.previous_question) }
    private val questionCounterTextView by lazy { findViewById<TextView>(R.id.question_counter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        // Get questions for the exam
        val questions: ArrayList<HashMap<String, Any>> =
            intent.extras!!["questions"] as ArrayList<HashMap<String, Any>>

        Log.d("questions", questions.toString())

        // Add first question
        nextQuestion(questions)

        // Initialize next and previous question on-click action
        nextQuestion.setOnClickListener {
            nextQuestion(questions)
        }
        previousQuestion.setOnClickListener {
            previousQuestion(questions)
        }
    }

    private fun setCounterTextView(total: Int) {
        questionCounterTextView.text = "Pitanje $counter / $total"
    }

    private fun nextQuestion(questions: ArrayList<java.util.HashMap<String, Any>>) {
        // If all questions have been answered, return
        if (counter >= questions.size) return
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        if (counter < questions.size)
            counter++
        // Set all components with proper values
        questionsTextView.text = currQuestion["question"].toString()
        ansAView.text = currQuestion["ansA"].toString()
        ansBView.text = currQuestion["ansB"].toString()
        ansCView.text = currQuestion["ansC"].toString()
        ansDView.text = currQuestion["ansD"].toString()

        // Set counter text
        setCounterTextView(questions.size)
    }

    private fun previousQuestion(questions: ArrayList<java.util.HashMap<String, Any>>) {
        // If counter is at first question, return
        if (counter == 0) return
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        counter--
        // Set all components with proper values
        questionsTextView.text = currQuestion["question"].toString()
        ansAView.text = currQuestion["ansA"].toString()
        ansBView.text = currQuestion["ansB"].toString()
        ansCView.text = currQuestion["ansC"].toString()
        ansDView.text = currQuestion["ansD"].toString()

        // Set counter text
        setCounterTextView(questions.size)
    }
}