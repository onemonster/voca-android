package com.tedilabs.voca.view.ui

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.commit
import com.google.android.gms.ads.AdRequest
import com.tedilabs.voca.R
import com.tedilabs.voca.lock.LockScreenService
import com.tedilabs.voca.network.service.VersionApiService
import com.tedilabs.voca.util.IntentUtil
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(R.layout.activity_main) {

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
    lateinit var versionApiService: VersionApiService

    private val lockViewModel: LockViewModel by viewModels()
    private val wordViewModel: WordViewModel by viewModels()

    private lateinit var overlayPermissionAlertDialog: AlertDialog

    private val isLockScreen: Boolean
        get() = intent.getBooleanExtra(LOCK_SCREEN_KEY, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("-_-_- onCreate $isLockScreen")

        loadBannerAd()

        wordViewModel.initialize()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                Timber.e(it)
            })

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
            .disposeOnDestroy()

        setupIfLockScreen()

        initializeViews()

        observeViewModels()

        overlayPermissionAlertDialog = AlertDialog.Builder(this)
            .setMessage(R.string.need_overlay_permission)
            .setPositiveButton(R.string.need_overlay_permission_positive) { _, _ ->
                getOverlayPermission()
            }
            .setNegativeButton(R.string.need_overlay_permission_negative) { _, _ -> }
            .setOnCancelListener { lockViewModel.turnLockScreenOff() }
            .create()

        if (!checkOverlayPermission()) {
            requestOverlayPermission()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Timber.d("-_-_- onNewIntent $isLockScreen")
        setupIfLockScreen()
    }

    override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    override fun onDestroy() {
        ad_view.destroy()
        super.onDestroy()
    }

    private fun setupIfLockScreen() {
        lockViewModel.setIsLockScreen(isLockScreen)

        if (!isLockScreen) return

        supportFragmentManager.findFragmentByTag(SettingFragment.TAG)?.let { settingFragment ->
            supportFragmentManager.commit {
                remove(settingFragment)
            }
        }

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

        wordViewModel.randomizeWord()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                Timber.e(it)
            })
    }

    private fun initializeViews() {
        supportFragmentManager.commit {
            add(R.id.main_fragment_container, MainFragment.create(), MainFragment.TAG)
        }
    }

    private fun observeViewModels() {
        lockViewModel.observeUseOnLockScreen()
            .subscribe({ lockScreenOn ->
                if (lockScreenOn) {
                    LockScreenService.start(this)
                } else {
                    LockScreenService.stop(this)
                }
            }, {
                Timber.e(it)
            })
            .disposeOnDestroy()
    }

    fun showSettings() {
        supportFragmentManager.commit {
            add(R.id.main_fragment_container, SettingFragment.create(), SettingFragment.TAG)
            addToBackStack(null)
        }
    }

    fun showWordLists() {
        supportFragmentManager.commit {
            add(R.id.main_fragment_container, WordListsFragment.create(), WordListsFragment.TAG)
            addToBackStack(null)
        }
    }

    fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            overlayPermissionAlertDialog.show()
        } else {
            getOverlayPermission()
        }
    }

    private fun loadBannerAd() {
        ad_view.loadAd(AdRequest.Builder().build())
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            // TOOD
            true
        }
    }

    private fun getOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    lockViewModel.turnLockScreenOn()
                    LockScreenService.start(this)
                } else {
                    lockViewModel.turnLockScreenOff()
                }
            }
        }
    }
}
