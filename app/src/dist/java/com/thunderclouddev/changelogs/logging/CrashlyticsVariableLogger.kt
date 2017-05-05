package com.thunderclouddev.changelogs.logging

import com.crashlytics.android.Crashlytics
import com.thunderclouddev.changelogs.preferences.BuildConfig
import com.thunderclouddev.changelogs.preferences.UserPreferences

/**
 * Created by david on 5/5/17.
 */
class CrashlyticsVariableLogger(private val preferences: UserPreferences,
                                private val buildConfig: BuildConfig) : VariableLogger.Logger {
    override fun storeBoolVariable(key: String, value: Boolean) {
        if (buildConfig.enableCrashlytics && preferences.allowAnonymousLogging) {
            Crashlytics.setBool(key, value)
        }
    }

    override fun storeDoubleVariable(key: String, value: Double) {
        if (buildConfig.enableCrashlytics && preferences.allowAnonymousLogging) {
            Crashlytics.setDouble(key, value)
        }
    }

    override fun storeFloatVariable(key: String, value: Float) {
        if (buildConfig.enableCrashlytics && preferences.allowAnonymousLogging) {
            Crashlytics.setFloat(key, value)
        }
    }

    override fun storeIntVariable(key: String, value: Int) {
        if (buildConfig.enableCrashlytics && preferences.allowAnonymousLogging) {
            Crashlytics.setInt(key, value)
        }
    }

    override fun storeStringVariable(key: String, value: String) {
        if (buildConfig.enableCrashlytics && preferences.allowAnonymousLogging) {
            Crashlytics.setString(key, value)
        }
    }
}