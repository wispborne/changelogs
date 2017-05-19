package com.thunderclouddev.changelogs

import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
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

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}