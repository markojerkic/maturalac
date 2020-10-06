package com.markojerkic.drzavnamatura

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.alespero.expandablecardview.ExpandableCardView
import com.bumptech.glide.Glide
import com.jsibbold.zoomage.ZoomageView
import io.github.kexanie.library.MathView

class ExamActivity : AppCompatActivity() {

    // Scroll view of the activity
    private val scrollView by lazy { findViewById<ScrollView>(R.id.exam_activity_scroll_view) }

    // Set up counter for the questions
    private var counter = -1

    // Find elements
    private val questionTextView by lazy { findViewById<TextView>(R.id.question_text_view) }
    private val questionImageView by lazy { findViewById<ZoomageView>(R.id.question_image) }

    // Expandable super question view
    private val superQuestionExpandView by lazy { findViewById<ExpandableCardView>(R.id.super_question_expandable_view) }
    private val superQuestionText by lazy{ superQuestionExpandView.findViewById<TextView>(R.id.super_question_text_view) }
    private val superQuestionMath by lazy { superQuestionExpandView.findViewById<MathView>(R.id.super_question_math_view) }
    private val superImageView by lazy { superQuestionExpandView.findViewById<ZoomageView>(R.id.super_question_image) }

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
    private val typeAnswerImage by lazy { findViewById<ZoomageView>(R.id.type_answer_image) }
    private var answerType = AnswerType.ABCD
    // Answers collection
    val answers = Answers()
    // Questions array list
    private lateinit var questions: ArrayList<Question>
    // Grade button
    private val gradeButton by lazy { findViewById<Button>(R.id.grade_button) }
    // State of exam: WORK (still doing the exam), GRADING (exam finished, looking over the resutls)
    private var examState: ExamState = ExamState.WORKING
    // Long answer image
    private val longAnswerImage by lazy { findViewById<ZoomageView>(R.id.long_answer_image) }

    // Open image elements
    private val imageDialog by lazy { Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) }
    private val dialogImageView by lazy { imageDialog.findViewById<ZoomageView>(R.id.large_image) }
    private val backButton by lazy { imageDialog.findViewById<ImageView>(R.id.image_back_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        // Get questions for the exam
        val examName = intent.extras!!["examName"] as String
        // Set current question
        questions = QuestionsObject.getQuestions(examName)

        Log.d("questions", questions.toString())

        // Add first question
        nextQuestion()

        // Set abcd answer click listeners
        setABCDOnClickListeners()
        // Set type answer update listener
        setTypeAnswer()

        // Initialize next and previous question on-click action
        nextQuestion.setOnClickListener {
            setTypeAnswer()
            nextQuestion()
        }
        previousQuestion.setOnClickListener {
            setTypeAnswer()
            previousQuestion()
        }

        // Add onClickListener for the grade button
        gradeButton.setOnClickListener {
            setTypeAnswer()
            gradeExam()
        }

        // Full image dialog
        imageDialog.setContentView(R.layout.open_image_dialog)
        questionImageView.setOnClickListener { openLargeImage(0) }
        backButton.setOnClickListener { imageDialog.dismiss()
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()}

        // Set on click action for all image views
        longAnswerImage.setOnClickListener { openLargeImage(1) }
        typeAnswerImage.setOnClickListener { openLargeImage(1) }
        superImageView.setOnClickListener { openLargeImage(2) }

    }

    private fun openLargeImage(imageViewNumber: Int) {
        when (imageViewNumber) {
            0 -> Glide.with(this).load(ImagesSingleton.getByteArray(questions[counter].id)).into(dialogImageView)
            1 -> Glide.with(this).load(ImagesSingleton.getAnswerByteArray(questions[counter].id)).into(dialogImageView)
            2 -> Glide.with(this).load(ImagesSingleton.getSuperByteArray(questions[counter].superImageName()!!)).into(dialogImageView)
        }
        imageDialog.show()
    }

    private fun gradeExam() {
        // Show toast announcement
        Toast.makeText(this, getString(R.string.grading_toast), Toast.LENGTH_SHORT).show()
        examState = ExamState.GRADING
        // Reset counter to 1 (start again from the first question)
        counter = 0
        disableAnswers()
        setQuestion(questions[counter], questions.size)
    }

    private fun disableAnswers() {
        ansABox.isClickable = false
        ansBBox.isClickable = false
        ansCBox.isClickable = false
        ansDBox.isClickable = false
        gradeButton.isClickable = false
        gradeButton.visibility = View.GONE
        //typeAnswerEditText.inputType = InputType.TYPE_NULL
        typeAnswerEditText.isEnabled = false
        // Scroll to the top
        scrollView.fullScroll(ScrollView.FOCUS_UP)
        scrollView.smoothScrollTo(0,0)
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

    private fun nextQuestion() {
        // If all questions have been answered, return
        if (counter >= questions.size-1)
            return
        counter++
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size)
    }

    private fun previousQuestion() {
        // If counter is at first question, return
        if (counter <= 0)
            return
        counter--
        // Create instance of the current questions, increase the counter
        val currQuestion = questions[counter]
        // Set all components with proper values
        setQuestion(currQuestion, questions.size)
    }

    private fun setQuestion(currQuestion: Question, total: Int) {
        if (ImagesSingleton.containsKey(currQuestion.id)
            && currQuestion.imgURI != null) {
            Glide.with(this).load(ImagesSingleton.getByteArray(currQuestion.id)).into(questionImageView)
            questionImageView.visibility = View.VISIBLE
        } else {
            questionImageView.visibility = View.GONE
        }

        // Check if there is a super question
        if (currQuestion.superQuestion() != null) {
            setSuperQuestion(currQuestion)
            superQuestionExpandView.visibility = View.VISIBLE
        } else {
            superQuestionExpandView.visibility = View.GONE
        }
        // If question contains latex, display in math view, else display text view
        if (currQuestion.question.contains("\\(")) {
            if (currQuestion.question.contains("−")) {
                currQuestion.question.replace("−", "-")
            }
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
                typeAnswerCorrectText.text = "Točan odgovor:\n\n" + currQuestion.ansA
                typeAnswerEditText.hint = "Niste dali odgovor!"

                if (currQuestion.ansImg != null
                    && examState == ExamState.GRADING
                    && ImagesSingleton.containsAnswerKey(currQuestion.id)) {
                    Glide.with(this).load(ImagesSingleton.getAnswerByteArray(currQuestion.id)).into(typeAnswerImage)
                    typeAnswerImage.visibility = View.VISIBLE
                } else {
                    typeAnswerImage.visibility = View.GONE
                }
            } else {
                typeAnswerImage.visibility = View.GONE
            }
        } else if (currQuestion.typeOfAnswer == AnswerType.LONG) {
            // If ViewSwitcher is not displaying 'long' answer, display it
            if (answerType != AnswerType.LONG) {
                longAnswerBar.visibility = View.VISIBLE
                typeAnswerBar.visibility = View.GONE
                abcdAnswerBar.visibility = View.GONE
                answerType = AnswerType.LONG
            }
            // Add answer image if exists, else make the ImageView GONE
            ImagesSingleton.printAns()
            if (currQuestion.ansImg != null
                && examState == ExamState.GRADING
                && ImagesSingleton.containsAnswerKey(currQuestion.id)) {
                Glide.with(this).load(ImagesSingleton.getAnswerByteArray(currQuestion.id)).into(longAnswerImage)
                longAnswerImage.visibility = View.VISIBLE
            } else {
                longAnswerImage.visibility = View.GONE
            }
        }

        // Set counter text
        setCounterTextView(total)
    }

    private fun setSuperQuestion(currQuestion: Question) {
        if (!currQuestion.superQuestion()!!.contains("\\(")) {
            superQuestionMath.visibility = View.GONE
            superQuestionText.visibility = View.VISIBLE
            superQuestionText.text = currQuestion.superQuestion()
        } else {
            superQuestionMath.visibility = View.VISIBLE
            superQuestionText.visibility = View.GONE
            superQuestionMath.text = currQuestion.superQuestion()
        }
        // Check for image
        if (currQuestion.superImgExists()) {
            if (ImagesSingleton.containsSuperImage(currQuestion.superImageName()!!)) {
                superImageView.visibility = View.VISIBLE
                Glide.with(this)
                    .load(ImagesSingleton.getSuperByteArray(currQuestion.superImageName()!!))
                    .into(superImageView)
            }
        } else {
            superImageView.visibility = View.GONE
        }

        //superQuestionExpandView.minimumHeight = height
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