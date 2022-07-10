package com.markojerkic.drzavnamatura

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainActivity.newInstance())
                .commitNow()
        }
    }
}