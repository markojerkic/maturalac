package com.markojerkic.drzavnamatura

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
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
    // ViewSwitcher for changing between types of answer entries
    private val viewSwitcher by lazy { findViewById<ViewSwitcher>(R.id.question_view_switcher) }
    private var answerType = AnswerType.ABCD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        // Get questions for the exam
        val questions: ArrayList<Question> =
            intent.extras!!["questions"] as ArrayList<Question>

        // Get images for the exam
        val questionImages: QuestionImages = intent.extras!!["questionImages"] as QuestionImages

        Log.d("questions", questions.toString())

        // Add first question
        nextQuestion(questions, questionImages)

        // Initialize next and previous question on-click action
        nextQuestion.setOnClickListener {
            nextQuestion(questions, questionImages)
        }
        previousQuestion.setOnClickListener {
            previousQuestion(questions, questionImages)
        }
    }

    private fun setCounterTextView(total: Int) {
        questionCounterTextView.text = "Pitanje ${counter+1} / $total"
    }

    private fun nextQuestion(questions: ArrayList<Question>, questionImages: QuestionImages) {
        // If all questions have been answered, returni
        if (counter >= questions.size-1)
            return
        counter++
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size, questionImages)
    }

    private fun previousQuestion(questions: ArrayList<Question>, questionImages: QuestionImages) {
        // If counter is at first question, return
        if (counter <= 0)
            return
        counter--
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size, questionImages)
    }

    private fun setQuestion(currQuestion: Question, total: Int, questionImages: QuestionImages) {
        if (questionImages.images.containsKey(currQuestion.id) && currQuestion.imgURI != null) {
            Glide.with(this).load(questionImages.images[currQuestion.id]).into(questionImageView)
            questionImageView.visibility = View.VISIBLE
        } else {
            questionImageView.visibility = View.GONE
        }
        // If question contains latex, display in math view, else display text view
        if (currQuestion.question.contains("\\(")) {
            questionMathView.text = currQuestion.question
            questionMathView.visibility = View.VISIBLE
            questionTextView.visibility = View.GONE
        } else {
            questionTextView.text = currQuestion.question
            questionTextView.visibility = View.VISIBLE
            questionMathView.visibility = View.GONE
        }
        // If question type is ABCD
        if (currQuestion.typeOfAnswer == AnswerType.ABCD) {
            // If current answer type is not ABCD, then switch the view back to ABCD answers
            if (answerType != AnswerType.ABCD) {
                viewSwitcher.showNext()
                answerType = AnswerType.ABCD
            }
            // Answer a
            if (!currQuestion.ansA.contains("\\(")) {
                ansAText.text = currQuestion.ansA
                ansAText.visibility = View.VISIBLE
                ansAMath.visibility = View.GONE
            } else {
                ansAMath.text = currQuestion.ansA
                ansAMath.visibility = View.VISIBLE
                ansAText.visibility = View.GONE
            }
            // Answer b
            if (!currQuestion.ansB.contains("\\(")) {
                ansBText.text = currQuestion.ansB
                ansBText.visibility = View.VISIBLE
                ansBMath.visibility = View.GONE
            } else {
                ansBMath.text = currQuestion.ansB
                ansBMath.visibility = View.VISIBLE
                ansBText.visibility = View.GONE
            }
            // Answer c
            if (!currQuestion.ansC.contains("\\(")) {
                ansCText.text = currQuestion.ansC
                ansCText.visibility = View.VISIBLE
                ansCMath.visibility = View.GONE
            } else {
                ansCMath.text = currQuestion.ansC
                ansCMath.visibility = View.VISIBLE
                ansCText.visibility = View.GONE
            }
            // Answer d
            if (!currQuestion.ansD.contains("\\(")) {
                ansDText.text = currQuestion.ansD
                ansDText.visibility = View.VISIBLE
                ansDMath.visibility = View.GONE
            } else {
                ansDMath.text = currQuestion.ansD
                ansDMath.visibility = View.VISIBLE
                ansDText.visibility = View.GONE
            }
        } else {
            // If ViewSwitcher is displaying ABCD answer, switch to type entry answer
            if (answerType != AnswerType.TYPE) {
                viewSwitcher.showNext()
                answerType = AnswerType.TYPE
            }
        }

        // Set counter text
        setCounterTextView(total)
    }
}