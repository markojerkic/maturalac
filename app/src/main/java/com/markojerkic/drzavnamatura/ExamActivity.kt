package com.markojerkic.drzavnamatura

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import io.github.kexanie.library.MathView

class ExamActivity : AppCompatActivity() {

    // Set up counter for the questions
    private var counter = -1

    // Find elements
    private val questionTextView by lazy { findViewById<TextView>(R.id.question_text_view) }
    private val questionImageView by lazy { findViewById<ImageView>(R.id.question_image) }

    // ABCD answer boxes
    private val ansABox by lazy { findViewById<ConstraintLayout>(R.id.ans_a_box) }
    private val ansBBox by lazy { findViewById<ConstraintLayout>(R.id.ans_b_box) }
    private val ansCBox by lazy { findViewById<ConstraintLayout>(R.id.ans_c_box) }
    private val ansDBox by lazy { findViewById<ConstraintLayout>(R.id.ans_d_box) }

    //Text views
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
    private val answerBarConstraintLayout by lazy { findViewById<ConstraintLayout>(R.id.question_type_switcher_constraint_layout) }
    private val abcdAnswerBar by lazy { findViewById<LinearLayout>(R.id.abcd_answer_constraint_layout) }
    private val typeAnswerBar by lazy { findViewById<LinearLayout>(R.id.type_answer_constraint_layout) }
    private val longAnswerBar by lazy { findViewById<LinearLayout>(R.id.long_answer_constraint_layout) }
    // Type answer EditText
    private val typeAnswerEditText by lazy { findViewById<EditText>(R.id.type_answer_edit_text) }
    private val typeAnswerCorrectText by lazy { findViewById<TextView>(R.id.type_ans_correct_ans)}
    private var answerType = AnswerType.ABCD
    // Answers collection
    val answers = Answers()
    // Questions array list
    val questions = arrayListOf<Question>()
    // Grade button
    private val gradeButton by lazy { findViewById<Button>(R.id.grade_button) }
    // State of exam: WORK (still doing the exam), GRADING (exam finished, looking over the resutls)
    private var examState: ExamState = ExamState.WORKING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        // Get questions for the exam
        val tempQ: ArrayList<Question> =
            intent.extras!!["questions"] as ArrayList<Question>
        // Set current question
        questions.addAll(tempQ)

        // Get images for the exam
        val imagesSingleton = ImagesSingleton

        Log.d("questions", questions.toString())

        // Add first question
        nextQuestion(imagesSingleton)

        // Set abcd answer click listeners
        setABCDOnClickListeners()
        // Set type answer update listener
        setTypeAnswer()

        // Initialize next and previous question on-click action
        nextQuestion.setOnClickListener {
            setTypeAnswer()
            nextQuestion(imagesSingleton)
        }
        previousQuestion.setOnClickListener {
            setTypeAnswer()
            previousQuestion(imagesSingleton)
        }

        // Add onClickListener for the grade button
        gradeButton.setOnClickListener {
            setTypeAnswer()
            gradeExam()
        }
    }

    private fun gradeExam() {
        Log.i("garde", "exam")
        val abcdAnswers = answers.getABCDAnswers()
        examState = ExamState.GRADING
        // Reset counter to 1 (start again from the first question)
        counter = 0
        disableAnswers()
        setQuestion(questions[counter], questions.size, ImagesSingleton)
    }

    private fun disableAnswers() {
        ansABox.isClickable = false
        ansBBox.isClickable = false
        ansCBox.isClickable = false
        ansDBox.isClickable = false
        gradeButton.isClickable = false
        //typeAnswerEditText.inputType = InputType.TYPE_NULL
        typeAnswerEditText.isEnabled = false
    }

    private fun setTypeAnswer() {
        // When previous or next question buttons are clicked,
        // then check if current question's answer is of type 'TYPE'
        // If yes, then add it to answers
        if (questions[counter].typeOfAnswer == AnswerType.TYPE) {
            answers.add(questions[counter], typeAnswerEditText.text.toString())
        }
    }

    private fun setABCDOnClickListeners() {
        ansABox.setOnClickListener {
            if (questions[counter].typeOfAnswer == AnswerType.ABCD) {
                answers.add(questions[counter], 0)
                setABCDAnswerClickedState(0)
            }
        }
        ansBBox.setOnClickListener {
            if (questions[counter].typeOfAnswer == AnswerType.ABCD) {
                answers.add(questions[counter], 1)
                setABCDAnswerClickedState(1)
            }
        }
        ansCBox.setOnClickListener {
            if (questions[counter].typeOfAnswer == AnswerType.ABCD) {
                answers.add(questions[counter], 2)
                setABCDAnswerClickedState(2)
            }
        }
        ansDBox.setOnClickListener {
            if (questions[counter].typeOfAnswer == AnswerType.ABCD) {
                answers.add(questions[counter], 3)
                setABCDAnswerClickedState(3)
            }
        }
    }

    // Set background color of answer box
    private fun setABCDAnswerClickedState(ans: Int) {
        when (ans) {
            0 -> colorBoxes(ansABox, ansBBox, ansCBox, ansDBox)
            1 -> colorBoxes(ansBBox, ansABox, ansCBox, ansDBox)
            2 -> colorBoxes(ansCBox, ansABox, ansBBox, ansDBox)
            3 -> colorBoxes(ansDBox, ansABox, ansBBox, ansCBox)
            else -> clearABCDBoxes()
        }
    }

    private fun clearABCDBoxes() {
        ansABox.setBackgroundResource(R.drawable.exam_question_cards)
        ansBBox.setBackgroundResource(R.drawable.exam_question_cards)
        ansCBox.setBackgroundResource(R.drawable.exam_question_cards)
        ansDBox.setBackgroundResource(R.drawable.exam_question_cards)
    }

    private fun colorBoxes (correct: View, i1: View, i2: View, i3: View) {
        correct.setBackgroundResource(R.drawable.exam_card_clicked)
        i1.setBackgroundResource(R.drawable.exam_question_cards)
        i2.setBackgroundResource(R.drawable.exam_question_cards)
        i3.setBackgroundResource(R.drawable.exam_question_cards)
    }

    private fun setCorrectABCD(correct: View, i1: View, i2: View, i3: View) {
        correct.setBackgroundResource(R.drawable.exam_card_green)
        i1.setBackgroundResource(R.drawable.exam_question_cards)
        i2.setBackgroundResource(R.drawable.exam_question_cards)
        i3.setBackgroundResource(R.drawable.exam_question_cards)
    }

    private fun setWrongABCD(wrong: View) {
        wrong.setBackgroundResource(R.drawable.exam_card_red)
    }

    private fun setCounterTextView(total: Int) {
        questionCounterTextView.text = "Pitanje ${counter+1} / $total"
    }

    private fun nextQuestion(imagesSingleton: ImagesSingleton) {
        // If all questions have been answered, return
        if (counter >= questions.size-1)
            return
        counter++
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size, imagesSingleton)
    }

    private fun previousQuestion(imagesSingleton: ImagesSingleton) {
        // If counter is at first question, return
        if (counter <= 0)
            return
        counter--
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size, imagesSingleton)
    }

    private fun setQuestion(currQuestion: Question, total: Int, imagesSingleton: ImagesSingleton) {
        if (imagesSingleton.containsKey(currQuestion.id)
            && currQuestion.imgURI != null
            && currQuestion.typeOfAnswer != AnswerType.LONG) {
            Glide.with(this).load(imagesSingleton.getByteArray(currQuestion.id)).into(questionImageView)
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
                abcdAnswerBar.visibility = View.VISIBLE
                typeAnswerBar.visibility = View.GONE
                longAnswerBar.visibility = View.GONE
                answerType = AnswerType.ABCD
            }
            // Set if answer if given
            if (examState == ExamState.WORKING) {
                if (answers.containsAnswer(currQuestion))
                    setABCDAnswerClickedState(answers.getAns(currQuestion) as Int)
                else clearABCDBoxes()
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
            if (examState == ExamState.GRADING) {
                gradeABCD(currQuestion)
            }
        } else if (currQuestion.typeOfAnswer == AnswerType.TYPE) {
            // If ViewSwitcher is not displaying 'type' answer, switch to type entry answer
            if (answerType != AnswerType.TYPE) {
                typeAnswerBar.visibility = View.VISIBLE
                abcdAnswerBar.visibility = View.GONE
                longAnswerBar.visibility = View.GONE
                answerType = AnswerType.TYPE
            }
            // Set EditText text if already given
            if (answers.containsAnswer(currQuestion))
                typeAnswerEditText.setText(answers.getAns(currQuestion) as String)
            else
                typeAnswerEditText.setText("")

            if (examState == ExamState.GRADING) {
                typeAnswerCorrectText.visibility = View.VISIBLE
                typeAnswerCorrectText.text = "Točan odgovor:\n" + currQuestion.ansA
            }
        } else if (currQuestion.typeOfAnswer == AnswerType.LONG) {
            // If ViewSwitcher is not displaying 'long' answer, display it
            if (answerType != AnswerType.LONG) {
                longAnswerBar.visibility = View.VISIBLE
                typeAnswerBar.visibility = View.GONE
                abcdAnswerBar.visibility = View.GONE
                answerType = AnswerType.LONG
            }
        }

        // Set counter text
        setCounterTextView(total)
    }

    private fun gradeABCD(currQuestion: Question) {
        if (answers.containsAnswer(currQuestion)) {
            val ans = answers.getAns(currQuestion) as Int
            checkCorrectAns(ans, currQuestion)
        } else {
            setCorrectABCDAns(currQuestion.correctAns.toInt())
        }
    }

    private fun setCorrectABCDAns(correctAns: Int) {
        when (correctAns) {
            0 -> setCorrectABCD(ansABox, ansBBox, ansCBox, ansDBox)
            1 -> setCorrectABCD(ansBBox, ansABox, ansCBox, ansDBox)
            2 -> setCorrectABCD(ansCBox, ansBBox, ansABox, ansDBox)
            3 -> setCorrectABCD(ansDBox, ansBBox, ansCBox, ansABox)
        }
    }

    private fun checkCorrectAns(ans: Int, currQuestion: Question) {
        setCorrectABCDAns(currQuestion.correctAns.toInt())
        if (ans != currQuestion.correctAns.toInt()) {
            when (ans) {
                0 -> setWrongABCD(ansABox)
                1 -> setWrongABCD(ansBBox)
                2 -> setWrongABCD(ansCBox)
                3 -> setWrongABCD(ansDBox)
            }
        }
    }
}