package com.markojerkic.drzavnamatura

import android.app.Dialog
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    // List of subjects and years in the database
    private val subjects = TreeSet<String>()
    // Map subject to the set of available years
    private val years = HashMap<String, TreeSet<String>>()
    // List of questions in the database
    private val questions = ArrayList<HashMap<String, Any>>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Initialize firebase app
        FirebaseApp.initializeApp(this)

        val db = Firebase.firestore

        // Read questions from the database
        db.collection("pitanja").get().addOnSuccessListener { result ->
            for (r in result) {
                val data = r.data

                // Add question to the list
                questions.add(data as HashMap<String, Any>)
                subjects.add(data["subject"].toString())

                // If no exams have been assigned to the subject, create new tree set
                // If there is already a tree set, just add the value
                if (years[data["subject"].toString()] != null)
                    years[data["subject"]]!!.add(data["year"].toString())
                else {
                    val tempTreeSet = TreeSet<String>()
                    tempTreeSet.add(data["year"].toString())
                    years[data["subject"].toString()] = tempTreeSet
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

        for ((c, s) in subjects.toList().withIndex()) {
            // Inflate the template view of the subjects
            val subjectView =  layoutInflater.inflate(R.layout.subject_title, leftRightLinearContainer,
                false) as TextView
            // Initialize the text title from the list of subjects
            subjectView.text = s
            // Initialize size to 170x170
            subjectView.width = 170
            subjectView.height = 170
            // Switch between left and right layout
            if (c % 2 == 0)
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
                examsListView.adapter = ExamsListAdapter(years[s]!!.toList(), layoutInflater)
                // Show the dialog
                dialog.show()
            }
        }

    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}