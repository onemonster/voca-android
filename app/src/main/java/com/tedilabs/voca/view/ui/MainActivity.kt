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
import androidx.fragment.app.commit
import com.tedilabs.voca.R
import com.tedilabs.voca.lock.LockScreenService
import com.tedilabs.voca.network.service.VersionApiService
import com.tedilabs.voca.preference.AppPreference
import com.tedilabs.voca.util.IntentUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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

    @Inject
    lateinit var versionApiService: VersionApiService

    private lateinit var overlayPermissionAlertDialog: AlertDialog

    private val isLockScreen: Boolean
        get() = intent.getBooleanExtra(LOCK_SCREEN_KEY, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("-_-_- onCreate $isLockScreen")

        // TODO: move to viewmodel
        versionApiService.getAppVersionStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.updateRequired) {
                    // TODO: handle case where user goes back without updating
                    AlertDialog.Builder(this)
                        .setMessage(R.string.update_required)
                        .setPositiveButton(R.string.update_required_positive) { dialog, _ ->
                            IntentUtil.startMarket(this)
                        }
                        .setCancelable(false)
                        .show()
                } else if (it.updateAvailable) {
                    // TODO: update available
                    AlertDialog.Builder(this)
                        .setMessage(R.string.update_available)
                        .setPositiveButton(R.string.update_available_positive) { dialog, _ ->
                            IntentUtil.startMarket(this)
                        }
                        .setNegativeButton(R.string.update_available_negative) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }, {
                Timber.e(it)
            })

        setupIfLockScreen()

        initializeViews()

        overlayPermissionAlertDialog = AlertDialog.Builder(this)
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
            overlayPermissionAlertDialog.show()
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
        supportFragmentManager.commit {
            add(R.id.main_fragment_container, MainFragment.create(), MainFragment.TAG)
        }
    }

    fun showSettings() {
        supportFragmentManager.commit {
            add(R.id.main_fragment_container, SettingFragment.create(), SettingFragment.TAG)
            addToBackStack(null)
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
                    overlayPermissionAlertDialog.show()
                } else {
                    appPreference.lockScreenOn = true
                    LockScreenService.start(this)
                }
            }
        }
    }
}
