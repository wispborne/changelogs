package com.thunderclouddev.changelogs

import com.facebook.stetho.Stetho
import com.thunderclouddev.changelogs.logging.CrashlyticsTree
import com.thunderclouddev.changelogs.logging.CrashlyticsVariableLogger
import timber.log.Timber

/**
 * Created by david on 5/3/17.
 */
class DistBaseApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()

        initLogging()
        Stetho.initializeWithDefaults(this)
        variableLogger.loggers.add(CrashlyticsVariableLogger(preferences, buildConfig))
    }

    override fun initLogging() {
        super.initLogging()

        if (preferences.allowAnonymousLogging && buildConfig.enableCrashlytics) {
            Timber.plant(CrashlyticsTree())
        }
    }
}