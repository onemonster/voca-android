package com.tedilabs.voca.view.ui

import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.activityViewModels
import com.tedilabs.voca.BuildConfig
import com.tedilabs.voca.R
import com.tedilabs.voca.lock.LockScreenService
import com.tedilabs.voca.util.IntentUtil
import kotlinx.android.synthetic.main.fragment_setting.*
import timber.log.Timber

class SettingFragment : BaseFragment(R.layout.fragment_setting) {

    companion object {
        const val TAG = "Setting"
        fun create() = SettingFragment()
    }

    private val lockViewModel: LockViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { context ->
            initializeViews(context)
            observeViewModels(context)
        }
    }

    private fun initializeViews(context: Activity) {
        back_button.setOnClickListener {
            context.onBackPressed()
        }

        feedback_container.setOnClickListener {
            IntentUtil.startFeedback(context)
        }

        review_container.setOnClickListener {
            IntentUtil.startMarket(context)
        }

        share_container.setOnClickListener {
            IntentUtil.startShareApp(context)
        }

        use_app_on_lock_screen_toggle.setOnClickListener {
            lockViewModel.toggleLockScreen()
        }

        app_version_text.text = BuildConfig.VERSION_NAME
    }

    private fun observeViewModels(activity: Activity) {
        lockViewModel.observeUseOnLockScreen()
            .subscribe({ lockScreenOn ->
                use_app_on_lock_screen_toggle.isChecked = lockScreenOn
                if (lockScreenOn) {
                    if (Settings.canDrawOverlays(activity)) {
                        LockScreenService.start(activity)
                    } else {
                        (activity as MainActivity).requestOverlayPermission()
                    }
                } else {
                    LockScreenService.stop(activity)
                }
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()
    }
}
