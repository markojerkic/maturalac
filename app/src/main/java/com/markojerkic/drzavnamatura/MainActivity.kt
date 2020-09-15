package com.markojerkic.drzavnamatura

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    // List of subjects and years in the database
    private val subjects = TreeSet<String>()
    // Map subject to the set of available years
    private val years = HashMap<String, TreeSet<String>>()
    // List of questions in the database
    private val questions = ArrayList<Question>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Initialize firebase app
        FirebaseApp.initializeApp(this)

        val db = Firebase.firestore
        val storageReference = Firebase.storage.reference

        // TODO: Add a background view while database is being downloaded: PREUZIMANJE
        // Read questions from the database
        db.collection("pitanja").get().addOnSuccessListener { result ->
            for (r in result) {
                val data = r.data

                // Add question to the list
                questions.add(Question(data as Map<String, Any>, r.id))
                subjects.add(questions.last().subject)

                // If no exams have been assigned to the subject, create new tree set
                // If there is already a tree set, just add the value
                if (years[questions.last().subject] != null)
                    years[questions.last().subject]!!.add(questions.last().year)
                else {
                    val tempTreeSet = TreeSet<String>()
                    tempTreeSet.add(questions.last().year)
                    years[questions.last().subject] = tempTreeSet
                }
            }

            // Create test subjects for ui
            inflateSubjects()
        }.addOnFailureListener {e -> Log.e("Firestore exception", e.toString())}

        // Create test subjects to test the ui
        inflateSubjects()
    }


    // Subjects are displayed in a 2 x n grid
    // THe grid is created with two vertical linear layouts (left and right) which sit inside
    // a master linear layout which is horizontal
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inflateSubjects() {
         // Create objects of layouts and the container of the left and right layout
        val leftRightLinearContainer = findViewById<LinearLayout>(R.id.leftRightLinearLayout)
        val leftLinearLayout = findViewById<LinearLayout>(R.id.left_linearLayout)
        val rightLinearLayout = findViewById<LinearLayout>(R.id.right_linearLayout)

        for ((index, subject) in subjects.toList().withIndex()) {
            // Inflate the template view of the subjects
            val subjectView =  layoutInflater.inflate(R.layout.subject_title, leftRightLinearContainer,
                false) as TextView
            // Initialize the text title from the list of subjects
            subjectView.text = subject
            // Initialize size to 170x170
            subjectView.width = 170
            subjectView.height = 170
            // Switch between left and right layout
            if (index % 2 == 0)
                leftLinearLayout.addView(subjectView)
            else
                rightLinearLayout.addView(subjectView)

            // Set on click action for the view
            subjectView.setOnClickListener {
                // Dialog pops up when a subject is clicked which shows which exams are available
                // for that subject
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.subject_list_dialog)
                // Background is a rectangle with rounded edges, so we have to set the
                // "background under the background" to be transparent, or else
                // it shows up as white edges
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Initialize the list view
                val examsListView = dialog.findViewById<ListView>(R.id.exams_list_view)
                // Set the adapter
                examsListView.adapter = ExamsListAdapter(years[subject]!!.toList(), layoutInflater)

                // Create onclick listener which will open new activity qith questions from
                // the chosen exam
                examsListView.setOnItemClickListener {parent, view, position, id ->
                    val chosenYear = years[subject]!!.toArray()[position]
                    val examQuestions = getExamQuestion(chosenYear as String, subject)

                    // Download images if exist
                    val questionImages = QuestionImages(examQuestions)
                    questionImages.checkQuestions(object: QuestionImagesProcessedCallback {
                        @Override
                        override fun done() {
                            startExamActivity(examQuestions, questionImages)
                        }
                    })
                }
                // Show the dialog
                dialog.show()
            }
        }

    }

    private fun startExamActivity(examQuestions: ArrayList<Question>, questionImages: QuestionImages) {
        // Start new activity, pass the questions through the intent
        val examActivityIntent = Intent(this, ExamActivity::class.java).apply {
            putExtra("questions", examQuestions)
        }
        startActivity(examActivityIntent)

    }

    private fun getExamQuestion(chosenYear: String, chosenSubject: String): ArrayList<Question> {
        val examQuestion = ArrayList<Question>()

        for (question in questions) {
            if (question.year == chosenYear && question.subject == chosenSubject)
                examQuestion.add(question)
        }
        return examQuestion

    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}