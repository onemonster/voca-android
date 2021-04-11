package com.tedilabs.voca.view.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tedilabs.voca.BuildConfig
import com.tedilabs.voca.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(R.layout.activity_setting) {

    companion object {
        fun intent(context: Context) = Intent(context, SettingActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeViews()
    }

    private fun initializeViews() {
        back_button.setOnClickListener {
            onBackPressed()
        }
        app_version_text.text = BuildConfig.VERSION_NAME
    }
}
