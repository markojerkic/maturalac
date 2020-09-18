package com.markojerkic.drzavnamatura

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
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
    // Icon which is shown while subjects are downloaded
    private val downloadingIcon by lazy { findViewById<LinearLayout>(R.id.loading_subjects_icon) }
    // Name TextView
    private val nameTextView by lazy { findViewById<TextView>(R.id.username_textview) }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Initialize firebase app
        FirebaseApp.initializeApp(this)

        val db = Firebase.firestore
        val storageReference = Firebase.storage.reference

        // Read questions from the database
        db.collection("pitanja").get().addOnSuccessListener { result ->
            // List of allowed subjects
            val allowed = resources.getStringArray(R.array.allowed_exams)
            for (r in result) {
                val data = r.data

                if (data["subject"] in allowed) {
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
            }

            // Remove the downloading icon and display subjects
            downloadingIcon.visibility = View.GONE
            inflateSubjects()
        }.addOnFailureListener {e -> Log.e("Firestore exception", e.toString())}


        // Shared preferences for storing user name
        val sharedPreferences = this.getSharedPreferences("myprefs", 0)
        var name = sharedPreferences.getString("name", null)
        if (name == null) {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.name_enter_dialog)
            // Background is a rectangle with rounded edges, so we have to set the
            // "background under the background" to be transparent, or else
            // it shows up as white edges
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val enterEditText = dialog.findViewById<EditText>(R.id.enter_name_edit_text)
            val enterButton = dialog.findViewById<Button>(R.id.enter_name_button)

            enterButton.setOnClickListener {
                name = enterEditText.text.toString()
                sharedPreferences.edit().putString("name", name).apply()
                setName(name!!)
                dialog.dismiss()
            }
            dialog.show()

        } else {
            setName(name!!)
        }

        // Create test subjects to test the ui
        inflateSubjects()
    }

    // Set name in the title of app
    private fun setName(name: String) {
        nameTextView.text = name
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

                // Create onclick listener which will open new activity with questions from
                // the chosen exam
                examsListView.setOnItemClickListener { _, _, position, _ ->
                    if (checkInternetConnection()) {
                        // Show info
                        Toast.makeText(this, getString(R.string.downloading_exam_toast), Toast.LENGTH_LONG).show()
                        val chosenYear = years[subject]!!.toArray()[position]
                        val examQuestions = arrayListOf<Question>()
                        examQuestions.addAll(getExamQuestion(chosenYear as String, subject)
                            .sortedWith((compareBy { it -> it.questionNumber }))
                        )

                        // Download images if exist
                        val questionImages = QuestionImages(examQuestions)
                        questionImages.checkQuestions(object : QuestionImagesProcessedCallback {
                            @Override
                            override fun done() {
                                startExamActivity(examQuestions)
                            }
                        })
                    } else {
                        Toast.makeText(this, getString(R.string.no_internet_toast), Toast.LENGTH_LONG).show()
                    }
                }
                // Show the dialog
                dialog.show()
            }
        }

    }

    // Check if there is an internet connection
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun startExamActivity(examQuestions: ArrayList<Question>) {
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

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                val infoDialog = Dialog(this)
                infoDialog.setContentView(R.layout.about_dialog)
                // Background is a rectangle with rounded edges, so we have to set the
                // "background under the background" to be transparent, or else
                // it shows up as white edges
                infoDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                infoDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}
