package com.markojerkic.drzavnamatura

import android.app.Dialog
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.jsibbold.zoomage.ZoomageView
import io.github.kexanie.library.MathView
import net.cachapa.expandablelayout.ExpandableLayout

class ExamActivity : AppCompatActivity() {

    // Scroll view of the activity
    private val scrollView by lazy { findViewById<ScrollView>(R.id.exam_activity_scroll_view) }

    // Set up counter for the questions
    private var counter = -1

    // Find elements
    private val questionTextView by lazy { findViewById<TextView>(R.id.question_text_view) }
    private val questionImageView by lazy { findViewById<ImageView>(R.id.question_image) }

    // Expandable super question view
    private val expandLinearWraper by lazy { findViewById<LinearLayout>(R.id.exand_liner_wraper) }
    private val clickToExpand by lazy { findViewById<ConstraintLayout>(R.id.click_to_expand) }
    private val superQuestionExpandableLayout by lazy { findViewById<ExpandableLayout>(R.id.expandable_super_question) }
    private val superQuestionText by lazy { findViewById<TextView>(R.id.super_question_text_view) }
    private val superQuestionMath by lazy { findViewById<MathView>(R.id.super_question_math_view) }
    private val superImageView by lazy { findViewById<ImageView>(R.id.super_question_image) }

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
    private val typeAnswerCorrectText by lazy { findViewById<TextView>(R.id.type_ans_correct_ans) }
    private val typeAnswerImage by lazy { findViewById<ImageView>(R.id.type_answer_image) }
    private val typeAnswerMath by lazy { findViewById<MathView>(R.id.type_ans_mathview) }
    private var answerType = AnswerType.ABCD

    // Answers collection
    val answers = Answers()

    // Questions array list
    private lateinit var questions: ArrayList<Question>

    // Grade button
    private val gradeFullButton by lazy { findViewById<Button>(R.id.grade_button) }
    private val gradeQuestion by lazy { findViewById<Button>(R.id.grade_one) }

    // State of exam: WORK (still doing the exam), GRADING (exam finished, looking over the resutls)
    private var examState: ExamState = ExamState.WORKING

    // Long answer image
    private val longAnswerImage by lazy { findViewById<ImageView>(R.id.long_answer_image) }

    // Open image elements
    private val imageDialog by lazy {
        Dialog(
            this,
            android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen
        )
    }
    private val dialogImageView by lazy { imageDialog.findViewById<ZoomageView>(R.id.large_image) }
    private val backButton by lazy { imageDialog.findViewById<ImageView>(R.id.image_back_button) }

    // Media player
    private val mediaPlayerView by lazy { findViewById<View>(R.id.media_player) }
    private val mediaPlayPause by lazy { findViewById<ImageView>(R.id.play_pause_icon) }
    private val mediaSeekbar by lazy { findViewById<SeekBar>(R.id.media_seekbar) }
    private val mediaProgressBar by lazy { findViewById<ProgressBar>(R.id.audio_progressbar) }
    private lateinit var mediaPlayer: MediaPlayer
    private var mediaReleased = true
    lateinit var seekbarRunnable: Runnable
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var currentlyPlaying = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        val adRequest = AdRequest.Builder().build()


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
            if (examState == ExamState.GRADE_ONE) {
                examState = ExamState.WORKING
                clearABCDBoxes()
            }
            setTypeAnswer()
            nextQuestion()
        }
        previousQuestion.setOnClickListener {
            if (examState == ExamState.GRADE_ONE) {
                examState = ExamState.WORKING
                clearABCDBoxes()
            }
            setTypeAnswer()
            previousQuestion()
        }

        // Add onClickListener for the grade button
        gradeFullButton.setOnClickListener {
            setTypeAnswer()
            gradeExam()
        }
        gradeQuestion.setOnClickListener {
            setTypeAnswer()
            gradeOneQuestion()
        }

        // Full image dialog
        imageDialog.setContentView(R.layout.open_image_dialog)
        questionImageView.setOnClickListener { openLargeImage(0) }
        backButton.setOnClickListener { imageDialog.dismiss() }


        // Set on click action for all image views
        longAnswerImage.setOnClickListener { openLargeImage(1) }
        typeAnswerImage.setOnClickListener { openLargeImage(1) }
        superImageView.setOnClickListener { openLargeImage(2) }

        clickToExpand.setOnClickListener {
            if (superQuestionExpandableLayout.isExpanded) superQuestionExpandableLayout.collapse()
            else superQuestionExpandableLayout.expand()
        }

    }

    private fun gradeOneQuestion() {
        examState = ExamState.GRADE_ONE
        setQuestion(questions[counter], questions.size)

    }

    private fun openLargeImage(imageViewNumber: Int) {
        when (imageViewNumber) {
            0 -> Glide.with(this).load(FilesSingleton.getByteArray(questions[counter].id))
                .into(dialogImageView)
            1 -> Glide.with(this).load(FilesSingleton.getAnswerByteArray(questions[counter].id))
                .into(dialogImageView)
            2 -> Glide.with(this)
                .load(FilesSingleton.getSuperByteArray(questions[counter].superImageName()!!))
                .into(dialogImageView)
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
        gradeFullButton.isClickable = false
        gradeFullButton.visibility = View.GONE
        gradeQuestion.isClickable = false
        gradeQuestion.visibility = View.GONE
        //typeAnswerEditText.inputType = InputType.TYPE_NULL
        typeAnswerEditText.isEnabled = false
        // Scroll to the top
        scrollView.fullScroll(ScrollView.FOCUS_UP)
        scrollView.smoothScrollTo(0, 0)
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

    private fun colorBoxes(correct: View, i1: View, i2: View, i3: View) {
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
        questionCounterTextView.text = "Pitanje ${counter + 1} / $total"
    }

    private fun nextQuestion() {
        // If all questions have been answered, return
        if (counter >= questions.size - 1)
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
        if (currQuestion.audioFileName() == null ||
            (currentlyPlaying != ""
                    && currentlyPlaying != FilesSingleton.getAudioUri(currQuestion.id).toString())
        ) {
            // Release media player
            if (this::mediaPlayer.isInitialized)
                mediaPlayer.release()
            mediaPlayPause.setImageResource(R.drawable.ic_play_arrow)
            mediaSeekbar.progress = 0
            mediaReleased = true
            setPlayPauseOrProgress(true)
            currentlyPlaying = ""
            if (this::seekbarRunnable.isInitialized)
                handler.removeCallbacks(seekbarRunnable)
        }


        if (FilesSingleton.containsKey(currQuestion.id)
            && currQuestion.imgURI != null
        ) {
            Glide.with(this).load(FilesSingleton.getByteArray(currQuestion.id))
                .into(questionImageView)
            questionImageView.visibility = View.VISIBLE
        } else {
            questionImageView.visibility = View.GONE
        }

        // Check if there is a super question
        if (currQuestion.superQuestion() != null || FilesSingleton.containsAudio(currQuestion.id)) {
            if (currQuestion.superQuestion() != null)
                setSuperQuestion(currQuestion)

            // Check if audio file is there
            if (FilesSingleton.containsAudio(currQuestion.id)) {

                mediaPlayerView.visibility = View.VISIBLE
                if (currentlyPlaying == "") {
                    mediaPlayer = MediaPlayer()
                    mediaPlayPause.setOnClickListener {
                        if (this::mediaPlayer.isInitialized) {
                            if (!mediaPlayer.isPlaying || mediaReleased) {
                                mediaPlayPause.setImageResource(R.drawable.ic_pause)
                                if (!mediaReleased)
                                    mediaPlayer.start()
                                else {
                                    mediaPlayer.apply {
                                        setPlayPauseOrProgress(false)
                                        setAudioAttributes(
                                            AudioAttributes.Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                                .build()
                                        )
                                        setDataSource(
                                            applicationContext,
                                            FilesSingleton.getAudioUri(currQuestion.id)
                                        )
                                        currentlyPlaying =
                                            FilesSingleton.getAudioUri(currQuestion.id).toString()
                                        prepareAsync()
                                        setOnPreparedListener {
                                            start()
                                            pause()
                                            start()
                                            setPlayPauseOrProgress(true)
                                            mediaSeekbar.max = duration / 1000
                                            mediaReleased = false
                                            mediaSeekbar.setOnSeekBarChangeListener(object :
                                                SeekBar.OnSeekBarChangeListener {
                                                override fun onProgressChanged(
                                                    p0: SeekBar?,
                                                    progress: Int,
                                                    fromUser: Boolean
                                                ) {
                                                    if (fromUser)
                                                        seekTo(progress * 1000)
                                                }

                                                override fun onStartTrackingTouch(p0: SeekBar?) {}

                                                override fun onStopTrackingTouch(p0: SeekBar?) {}

                                            })
                                        }
                                    }
                                    if (this@ExamActivity::seekbarRunnable.isInitialized)
                                        this.runOnUiThread(seekbarRunnable)
                                    else {
                                        seekbarRunnable = object : Runnable {
                                            override fun run() {
                                                if (this@ExamActivity::mediaPlayer.isInitialized) {
                                                    if (!mediaReleased)
                                                        mediaSeekbar.progress =
                                                            mediaPlayer.currentPosition / 1000
                                                }
                                                handler.postDelayed(this, 1000)
                                            }

                                        }
                                    }
                                }

                            } else {
                                mediaPlayPause.setImageResource(R.drawable.ic_play_arrow)
                                mediaPlayer.pause()
                            }
                        }
                    }
                }
            } else mediaPlayerView.visibility = View.GONE

            expandLinearWraper.visibility = View.VISIBLE
        } else {
            expandLinearWraper.visibility = View.GONE
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
            if (examState == ExamState.GRADING || examState == ExamState.GRADE_ONE) {
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

            if (examState == ExamState.GRADING || examState == ExamState.GRADE_ONE) {
                if (currQuestion.ansA.contains("\\(")) {
                    typeAnswerMath.visibility = View.VISIBLE
                    typeAnswerCorrectText.visibility = View.GONE
                    typeAnswerMath.text = "Točan odgovor:\n\n" + currQuestion.ansA
                } else {
                    typeAnswerCorrectText.visibility = View.VISIBLE
                    typeAnswerMath.visibility = View.GONE
                    typeAnswerCorrectText.text = "Točan odgovor:\n\n" + currQuestion.ansA
                }
                typeAnswerEditText.hint = "Niste dali odgovor!"

                if (currQuestion.ansImg != null
                    && (examState == ExamState.GRADING || examState == ExamState.GRADE_ONE)
                    && FilesSingleton.containsAnswerKey(currQuestion.id)
                ) {
                    Glide.with(this).load(FilesSingleton.getAnswerByteArray(currQuestion.id))
                        .into(typeAnswerImage)
                    typeAnswerImage.visibility = View.VISIBLE
                } else {
                    typeAnswerImage.visibility = View.GONE
                }
            } else {
                typeAnswerImage.visibility = View.GONE
                typeAnswerMath.visibility = View.GONE
                typeAnswerCorrectText.visibility = View.GONE
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
            FilesSingleton.printAns()
            if (currQuestion.ansImg != null
                && (examState == ExamState.GRADING || examState == ExamState.GRADE_ONE)
                && FilesSingleton.containsAnswerKey(currQuestion.id)
            ) {
                Glide.with(this).load(FilesSingleton.getAnswerByteArray(currQuestion.id))
                    .into(longAnswerImage)
                longAnswerImage.visibility = View.VISIBLE
            } else {
                longAnswerImage.visibility = View.GONE
            }
        }

        // Set counter text
        setCounterTextView(total)
    }

    private fun setPlayPauseOrProgress(play: Boolean) {
        if (play) {
            mediaPlayPause.visibility = View.VISIBLE
            mediaProgressBar.visibility = View.INVISIBLE
        } else {
            mediaPlayPause.visibility = View.INVISIBLE
            mediaProgressBar.visibility = View.VISIBLE

        }
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
            if (FilesSingleton.containsSuperImage(currQuestion.superImageName()!!)) {
                superImageView.visibility = View.VISIBLE
                Glide.with(this)
                    .load(FilesSingleton.getSuperByteArray(currQuestion.superImageName()!!))
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

    @Override
    override fun onStop() {
        super.onStop()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.pause()
            //*currentlyPlaying = ""
            //mediaReleased = true

            mediaPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }
}