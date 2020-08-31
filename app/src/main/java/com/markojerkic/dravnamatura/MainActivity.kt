package com.markojerkic.dravnamatura

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.LineNumberReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val leftRightLinearContainer = findViewById<LinearLayout>(R.id.leftRightLinearLayout)
        val leftLinearLayout = findViewById<LinearLayout>(R.id.left_linearLayout)
        val rightLinearLayout = findViewById<LinearLayout>(R.id.right_linearLayout)

        val subjs = listOf("mat", "eng", "hrv", "fiz", "bio")

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
        }

    }
}