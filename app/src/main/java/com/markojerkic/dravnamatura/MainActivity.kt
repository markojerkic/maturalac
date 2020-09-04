package com.markojerkic.dravnamatura

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        createMockSubs()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun createMockSubs() {

        val leftRightLinearContainer = findViewById<LinearLayout>(R.id.leftRightLinearLayout)
        val leftLinearLayout = findViewById<LinearLayout>(R.id.left_linearLayout)
        val rightLinearLayout = findViewById<LinearLayout>(R.id.right_linearLayout)

        val subjs = listOf("mat", "eng", "hrv", "fiz", "bio")
        val adapter = ExamsListAdapter(listOf("2019. JESEN", "2029 LJETO", "2018 JESEN", "2018. LJETO"), layoutInflater)

        for ((c, s) in subjs.withIndex()) {
            val test =  layoutInflater.inflate(R.layout.subject_title, leftRightLinearContainer,
                false) as TextView
            test.text = s
            test.width = 170
            test.height = 170
            if (c % 2 == 0)
                leftLinearLayout.addView(test)
            else
                rightLinearLayout.addView(test)

            test.setOnClickListener {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.subject_list_dialog)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val examsListView = dialog.findViewById<ListView>(R.id.exams_list_view)
                examsListView.adapter = adapter


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