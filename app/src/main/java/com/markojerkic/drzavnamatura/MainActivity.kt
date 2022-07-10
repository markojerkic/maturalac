package com.markojerkic.drzavnamatura

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.markojerkic.drzavnamatura.model.Subject
import com.markojerkic.drzavnamatura.util.ApiServiceHolder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.cachapa.expandablelayout.ExpandableLayout


class MainActivity : Fragment(R.layout.activity_main) {

    companion object {
        fun newInstance() = MainActivity()
    }

    // Set if this is debug version
    private var IS_DEBUG = false

    // Icon which is shown while subjects are downloaded
    private val downloadingIcon by lazy { activity?.findViewById(R.id.loading_subjects_icon) as LinearLayout }

    // Name TextView
    private val nameTextView by lazy { activity?.findViewById(R.id.username_textview) as TextView }

    // Multi click event timing
    private var nameTextViewClickTime: Long = -1
    private var clickCounter = 0

    // Scroll view
    private val scrollView by lazy { activity?.findViewById(R.id.main_activity_scroll) as ScrollView }


    // Create objects of layouts and the container of the left and right layout
    private val subjectRowsLinearContainer by lazy { activity?.findViewById(R.id.subject_rows_linear_layout) as LinearLayout }

    // Database and storage reference
    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    // Progress dialog
    private val progressDialog by lazy { Dialog(requireContext()) }
    private val downloadProgressBar by lazy { progressDialog.findViewById(R.id.progress_bar) as ProgressBar }
    private val downloadProgressText by lazy { progressDialog.findViewById(R.id.progress_text) as TextView }

    // Exam list expand view trackers
    private lateinit var lastExpandedLayout: ExpandableLayout
    private lateinit var lastExpandedEntry: String
    private lateinit var lastClickedSubject: TextView
    private lateinit var lastMargin: ImageView

    // Firebase analitics
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var examsTreeSubscription: Disposable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize firebase app
        FirebaseApp.initializeApp(requireContext())
        firebaseAnalytics = Firebase.analytics

        // Check allowed subjects and exams
        //checkAllowedExams()

        fetchPublicExams()

        // Shared preferences for storing user name
        val sharedPreferences = requireActivity().getSharedPreferences("myprefs", 0)
        var name = sharedPreferences.getString("name", null)
        if (name == null) {
            val dialog = Dialog(requireContext())
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

        // Set multiclick to start debug
        setOnMultiTouchDebug()

    }

    override fun onDestroy() {
        super.onDestroy()
        examsTreeSubscription.dispose()
    }

    private fun fetchPublicExams() {
        examsTreeSubscription = ApiServiceHolder.getPublicExamsTree()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    if (it.ok && it.data != null) {
                        downloadingIcon.visibility = View.GONE
                        inflateSubjects(it.data!!)
                    } else {
//                        publicExams.postValue(null)
                    }
                },
                {
//                    publicExams.postValue(null)
                    it.localizedMessage?.let { it1 -> Log.e("exams-tree", it1) }
                },
                {
//                    isLoading.postValue(false)
                }
            )
    }

    private fun setOnMultiTouchDebug() {
        nameTextView.setOnClickListener {
            if (!IS_DEBUG) {
                clickCounter++
                clickCounter %= 7

                if (nameTextViewClickTime == -1L) {
                    nameTextViewClickTime = System.currentTimeMillis()
                } else if (clickCounter == 6) {
                    if (System.currentTimeMillis() - nameTextViewClickTime <= 3000L) {
                        IS_DEBUG = true
                        // Empty subject list
                        subjectRowsLinearContainer.removeAllViews()
                        // Check for allowed exams again
                        downloadingIcon.visibility = View.VISIBLE
                        // TODO šta je ovo
//                        checkAllowedExams()
                    }
                }
            }
        }
    }

    // Set name in the title of app
    private fun setName(name: String) {
        nameTextView.text = name
    }


    // Subjects are displayed in a 2 x n grid
    // THe grid is created with two vertical linear layouts (left and right) which sit inside
    // a master linear layout which is horizontal
    private fun inflateSubjects(allowed: List<Subject>) {

        lateinit var subjectRow: ConstraintLayout
        var firstEntry = true

        for ((index, entry) in allowed.withIndex()) {
            // Inflate the template view of the subjects
            if (firstEntry) {
                subjectRow = layoutInflater.inflate(
                    R.layout.subjects_row,
                    subjectRowsLinearContainer,
                    false
                ) as ConstraintLayout
                subjectRowsLinearContainer.addView(subjectRow)
            }

            // Find left and right subject
            val leftSubject = subjectRow.findViewById<TextView>(R.id.left_subject)
            val leftMargin = subjectRow.findViewById<ImageView>(R.id.left_margin)
            val rightSubject = subjectRow.findViewById<TextView>(R.id.right_subject)
            val rightMargin = subjectRow.findViewById<ImageView>(R.id.right_margin)
            val expandingExams =
                subjectRow.findViewById<ExpandableLayout>(R.id.exam_list_expandable)
            val examListLinearLayout =
                subjectRow.findViewById<LinearLayout>(R.id.exam_list_linearlayout)

            // Initialize the text title from the list of subjects
            if (firstEntry) {
                leftSubject.text = entry.subject
                firstEntry = false
                leftSubject.setOnClickListener {
                    setClickedBackground(leftSubject, leftMargin, entry.subject)
                    setSubjectOnClick(entry, examListLinearLayout, expandingExams)
                }
            } else {
                rightSubject.text = entry.subject
                firstEntry = true
                rightSubject.setOnClickListener {
                    setClickedBackground(rightSubject, rightMargin, entry.subject)
                    setSubjectOnClick(entry, examListLinearLayout, expandingExams)
                }
            }
            // Switch between left and right layout
            if (index == allowed.size - 1 && index % 2 == 0)
                rightSubject.visibility = View.INVISIBLE
            // Set on click action for the view
        }

    }

    private fun setClickedBackground(clicked: TextView?, margin: ImageView?, subject: String) {
        if (this::lastExpandedEntry.isInitialized) {
            if (lastExpandedEntry != subject) {
                if (this::lastClickedSubject.isInitialized) {
                    lastClickedSubject.setBackgroundResource(R.drawable.round_rectangle_subject_shape)
                    lastClickedSubject.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    lastMargin.visibility = View.INVISIBLE
                }
                clicked!!.setBackgroundResource(R.drawable.clicked_subject_shape)
                clicked.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                margin!!.visibility = View.VISIBLE
                lastClickedSubject = clicked
                lastMargin = margin
            } else {
                clicked!!.setBackgroundResource(R.drawable.round_rectangle_subject_shape)
                lastClickedSubject.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                margin!!.visibility = View.INVISIBLE
            }
        } else {
            clicked!!.setBackgroundResource(R.drawable.clicked_subject_shape)
            clicked.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            margin!!.visibility = View.VISIBLE
            lastClickedSubject = clicked
            lastMargin = margin
        }
    }

    private fun setSubjectOnClick(
        entry: Subject,
        examListView: LinearLayout, expandingExams: ExpandableLayout
    ) {
        // Set the adapter
        examListView.removeAllViews()
        inflateExams(examListView, entry)

        // Expand the list
        if (!this::lastExpandedEntry.isInitialized) {
            lastExpandedEntry = entry.subject
            lastExpandedLayout = expandingExams
            expandingExams.expand()
            scrollToExams(expandingExams)
        } else {
            when {
                lastExpandedEntry == entry.subject -> {
                    expandingExams.collapse()
                    lastExpandedEntry = ""
                }
                lastExpandedLayout.hashCode() == expandingExams.hashCode() -> {
                    Log.d("Adapter", "Adapter changed")
                    if (!expandingExams.isExpanded) expandingExams.expand()
                    scrollToExams(expandingExams)
                    lastExpandedEntry = entry.subject
                }
                else -> {
                    lastExpandedLayout.collapse()
                    expandingExams.expand()
                    lastExpandedLayout = expandingExams
                    lastExpandedEntry = entry.subject
                    scrollToExams(expandingExams)
                }
            }
        }
    }

    private fun scrollToExams(expandingExams: ExpandableLayout) {
        // TODO
        /*
        expandingExams.setOnExpansionUpdateListener { expansionFraction, state ->
            if (state == 3)
                scrollView.post {
                    scrollView.smoothScrollTo(0, expandingExams.y.toInt())
                }
        }

         */

    }

    private fun inflateExams(
        examListLinearLayout: LinearLayout,
        subject: Subject
    ) {
        for (exam in subject.exams) {
            val examLayout =
                layoutInflater.inflate(R.layout.exam_list_item, examListLinearLayout, false)
            val examTitle = examLayout.findViewById<TextView>(R.id.exam_name_textview)
            examTitle.text = exam
            examListLinearLayout.addView(examLayout)

            // Create onclick listener which will open new activity with questions from
            // the chosen exam
            examLayout.setOnClickListener {
                if (checkInternetConnection()) {
                    startExamActivity(subject.subject, exam)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_internet_toast), Toast.LENGTH_LONG)
                        .show()
                }

            }
        }
    }

    // Check if there is an internet connection
    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun startExamActivity(subject: String, exam: String) {
        // Show info
        Toast.makeText(requireContext(), getString(R.string.opening_exam_toast), Toast.LENGTH_LONG).show()
        // Start new activity, pass the questions through the intent
        val examActivityIntent = Intent(requireContext(), ExamActivity::class.java).apply {
            putExtra("subject", subject)
            putExtra("exam", exam)
        }
        startActivity(examActivityIntent)
    }

    private fun updateProgressDialog(percent: Double) {
        downloadProgressBar.progress = (100 * percent).toInt()
        downloadProgressText.text = "${(100 * percent).toInt()}%"
    }

//    @Override
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        return true
//    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.info -> {
                val infoDialog = Dialog(requireContext())
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
