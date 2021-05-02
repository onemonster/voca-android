package com.tedilabs.voca.view.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tedilabs.voca.R
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1000
    }

    @Inject
    lateinit var appPreference: AppPreference

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary
        subject_word_text.text = "사과"
        subject_type_text.text = "NOUN"
        subject_pronunciation_text.text = "[사:과]"
        mother_word_text.text = "APPLE"
        mother_example_text.text = "There is a shop which sells delicious apple."

        initializeViews()

        alertDialog = AlertDialog.Builder(this)
            .setMessage(R.string.need_overlay_permission)
            .setPositiveButton(R.string.need_overlay_permission_positive) { dialog, _ ->
                requestPermission()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.need_overlay_permission_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        if (!checkOverlayPermission()) {
            alertDialog.show()
        }
    }

    private fun initializeViews() {
        setting_button.setOnClickListener {
            startActivity(SettingActivity.intent(this))
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            // TOOD
            true
        }
    }

    private fun requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
        } else {
            // TODO
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    alertDialog.show()
                } else {
                    appPreference.lockScreenOn = true
                }
            }
        }
    }
}
