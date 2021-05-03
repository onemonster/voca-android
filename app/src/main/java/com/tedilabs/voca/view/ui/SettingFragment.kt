package com.tedilabs.voca.view.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.tedilabs.voca.BuildConfig
import com.tedilabs.voca.R
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment(R.layout.fragment_setting) {

    companion object {
        const val TAG = "Setting"
        fun create() = SettingFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { context ->
            initializeViews(context)
        }
    }

    private fun initializeViews(context: Activity) {
        back_button.setOnClickListener {
            context.onBackPressed()
        }
        app_version_text.text = BuildConfig.VERSION_NAME
    }
}
