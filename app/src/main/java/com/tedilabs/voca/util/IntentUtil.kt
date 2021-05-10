package com.tedilabs.voca.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.tedilabs.voca.BuildConfig
import com.tedilabs.voca.R


object IntentUtil {
    fun startMarket(context: Context, appPackageName: String = BuildConfig.APPLICATION_ID) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    fun startFeedback(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://forms.gle/1fWNFsCxfhqYzx6w7")
                )
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun startShareApp(context: Context, appPackageName: String = BuildConfig.APPLICATION_ID) {
        try {
            val shareAppString = context.getString(
                R.string.share_app,
                "https://play.google.com/store/apps/details?id=$appPackageName"
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareAppString)
                type = "text/plain"
            }
            val shareAppChooserString = context.getString(R.string.share_app_chooser)
            context.startActivity(Intent.createChooser(shareIntent, shareAppChooserString))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}
