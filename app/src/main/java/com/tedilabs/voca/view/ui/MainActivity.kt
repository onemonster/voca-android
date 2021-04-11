package com.tedilabs.voca.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tedilabs.voca.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Temporary
        subject_word_text.text = "사과"
        subject_type_text.text = "NOUN"
        subject_pronunciation_text.text = "[사:과]"
        mother_word_text.text = "APPLE"
        mother_example_text.text = "There is a shop which sells delicious apple."
    }
}
