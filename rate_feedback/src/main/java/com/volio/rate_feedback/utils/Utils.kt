package com.volio.rate_feedback.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.StrictMode
import android.util.DisplayMetrics
import android.widget.Toast
import java.util.Locale
import java.util.TimeZone

object Utils {
    fun openMarket(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        runCatching {
            intent.data = Uri.parse("market://details?id=${context.packageName}")
            context.startActivity(intent)
        }
    }

    fun sendEmail(
        context: Context,
        addresses: Array<String>,
        subject: String,
        body: String
    ) {

        disableExposure()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        intent.putExtra(
            Intent.EXTRA_TEXT, body + "\n\n\n" +
                    "DEVICE INFORMATION (Device information is useful for application improvement and development)"
                    + "\n\n" + getDeviceInfo()
        )

        runCatching {
            context.startActivity(intent)
        }.onFailure {
            Toast.makeText(context, "you need install gmail", Toast.LENGTH_SHORT).show()
        }
    }
    private fun disableExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            runCatching {
                val method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                method.invoke(null)
            } .onFailure {
                it.printStackTrace()
            }
        }
    }
    private fun getDeviceInfo(): String {
        val densityText = when (Resources.getSystem().displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> "HDPI"
        }

        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        var megAvailable = 0L
        val bytesAvailable: Long = stat.blockSizeLong * stat.availableBlocksLong
        megAvailable = bytesAvailable / (1024 * 1024)


        return "Manufacturer ${Build.MANUFACTURER}, Model ${Build.MODEL}," +
                " ${Locale.getDefault()}, " +
                "osVer ${Build.VERSION.RELEASE}, Screen ${Resources.getSystem().displayMetrics.widthPixels}x${Resources.getSystem().displayMetrics.heightPixels}, " +
                "$densityText, Free space ${megAvailable}MB, TimeZone ${
                    TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
                }"
    }
}