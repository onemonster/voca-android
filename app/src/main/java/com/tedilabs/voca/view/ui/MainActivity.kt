package com.tedilabs.voca.view.ui

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tedilabs.voca.R
import com.tedilabs.voca.lock.LockScreenService
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1000

        private const val LOCK_SCREEN_KEY = "lock-screen"

        fun intent(context: Context) = Intent(context, MainActivity::class.java)
        fun lockScreenIntent(context: Context) = intent(context).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(LOCK_SCREEN_KEY, true)
        }
    }

    @Inject
    lateinit var appPreference: AppPreference

    private lateinit var alertDialog: AlertDialog

    private val isLockScreen: Boolean
        get() = intent.getBooleanExtra(LOCK_SCREEN_KEY, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("-_-_- onCreate $isLockScreen")

        setupIfLockScreen()

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Timber.d("-_-_- onNewIntent $isLockScreen")
        setupIfLockScreen()
    }

    private fun setupIfLockScreen() {
        if (!isLockScreen) return

        Timber.d("-_-_- Setting up for lock screen")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
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
                    LockScreenService.start(this)
                }
            }
        }
    }
}
