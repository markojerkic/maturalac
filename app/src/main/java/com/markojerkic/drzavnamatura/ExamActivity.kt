package com.markojerkic.drzavnamatura

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.kexanie.library.MathView

class ExamActivity : AppCompatActivity() {

    // Set up counter for the questions
    private var counter = -1

    // Find elements
    private val questionTextView by lazy { findViewById<TextView>(R.id.question_text_view) }
    private val questionImageView by lazy { findViewById<ImageView>(R.id.question_image) }
    private val ansAText by lazy { findViewById<TextView>(R.id.answer_a_text) }
    private val ansBText by lazy { findViewById<TextView>(R.id.answer_b_text) }
    private val ansCText by lazy { findViewById<TextView>(R.id.answer_c_text) }
    private val ansDText by lazy { findViewById<TextView>(R.id.answer_d_text) }
    // Math views
    private val questionMathView by lazy { findViewById<MathView>(R.id.question_math_view) }
    private val ansAMath by lazy { findViewById<MathView>(R.id.answer_a_math) }
    private val ansBMath by lazy { findViewById<MathView>(R.id.answer_b_math) }
    private val ansCMath by lazy { findViewById<MathView>(R.id.answer_c_math) }
    private val ansDMath by lazy { findViewById<MathView>(R.id.answer_d_math) }
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
        questionCounterTextView.text = "Pitanje ${counter+1} / $total"
    }

    private fun nextQuestion(questions: ArrayList<java.util.HashMap<String, Any>>) {
        // If all questions have been answered, returni
        if (counter >= questions.size-1)
            return
        counter++
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size)
    }

    private fun previousQuestion(questions: ArrayList<java.util.HashMap<String, Any>>) {
        // If counter is at first question, return
        if (counter <= 0)
            return
        counter--
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size)
    }

    private fun setQuestion(currQuestion: java.util.HashMap<String, Any>, total: Int) {
        // If question contains latex, display in math view, else display text view
        if (currQuestion["question"].toString().contains("\\(")) {
            questionMathView.text = currQuestion["question"].toString()
            questionMathView.visibility = View.VISIBLE
            questionTextView.visibility = View.GONE
        } else {
            questionTextView.text = currQuestion["question"].toString()
            questionTextView.visibility = View.VISIBLE
            questionMathView.visibility = View.GONE
        }
        // Answer a
        if (!currQuestion["ansA"].toString().contains("\\(")) {
            ansAText.text = currQuestion["ansA"].toString()
            ansAText.visibility = View.VISIBLE
            ansAMath.visibility = View.GONE
        } else {
            ansAMath.text = currQuestion["ansA"].toString()
            ansAMath.visibility = View.VISIBLE
            ansAText.visibility = View.GONE
        }
        // Answer b
        if (!currQuestion["ansB"].toString().contains("\\(")) {
            ansBText.text = currQuestion["ansB"].toString()
            ansBText.visibility = View.VISIBLE
            ansBMath.visibility = View.GONE
        } else {
            ansBMath.text = currQuestion["ansB"].toString()
            ansBMath.visibility = View.VISIBLE
            ansBText.visibility = View.GONE
        }
        // Answer c
        if (!currQuestion["ansC"].toString().contains("\\(")) {
            ansCText.text = currQuestion["ansC"].toString()
            ansCText.visibility = View.VISIBLE
            ansCMath.visibility = View.GONE
        } else {
            ansCMath.text = currQuestion["ansC"].toString()
            ansCMath.visibility = View.VISIBLE
            ansCText.visibility = View.GONE
        }
        // Answer d
        if (!currQuestion["ansD"].toString().contains("\\(")) {
            ansDText.text = currQuestion["ansD"].toString()
            ansDText.visibility = View.VISIBLE
            ansDMath.visibility = View.GONE
        } else {
            ansDMath.text = currQuestion["ansD"].toString()
            ansDMath.visibility = View.VISIBLE
            ansDText.visibility = View.GONE
        }

        // Set counter text
        setCounterTextView(total)
    }
}