package com.thunderclouddev.changelogs.logging

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber

/**
 * Created by David Whitman on 2/24/2015.

 * @author David Whitman
 */
class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.INFO, Log.WARN -> Crashlytics.log(priority, tag, message)
            Log.ERROR -> {
                try {
                    if (t != null) {
                        Crashlytics.logException(t)
                    }

                    Crashlytics.log(priority, tag, message)
                } catch(exception: Exception) {
                    // wat do
                    Log.e("Crashlytics", "Failed to log using Crashlytics: ${exception.message}", exception)
                }
            }
        }
    }
}