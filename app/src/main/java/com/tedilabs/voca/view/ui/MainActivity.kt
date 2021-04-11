package com.tedilabs.voca.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tedilabs.voca.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary
        subject_word_text.text = "사과"
        subject_type_text.text = "NOUN"
        subject_pronunciation_text.text = "[사:과]"
        mother_word_text.text = "APPLE"
        mother_example_text.text = "There is a shop which sells delicious apple."

        initializeViews()
    }

    private fun initializeViews() {
        setting_button.setOnClickListener {
            startActivity(SettingActivity.intent(this))
        }
    }
}
