/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

@file:JvmName("LoggingUtils")

package com.thunderclouddev.changelogs.logging

import android.content.Context
import com.thunderclouddev.changelogs.preferences.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Used to store app-related variables to provide logging context, rather than specific events.
 * For example, the number of apps the user has installed.
 *
 * @author David Whitman
 */
@Singleton
class VariableLogger @Inject constructor(private val context: Context,
                                         private val buildConfig: BuildConfig) {
    val loggers: MutableList<Logger> = mutableListOf()


    fun storeBoolVariable(key: String, value: Boolean) {
        loggers.forEach { it.storeBoolVariable(key, value) }
    }

    fun storeDoubleVariable(key: String, value: Double) {
        loggers.forEach { it.storeDoubleVariable(key, value) }
    }

    fun storeFloatVariable(key: String, value: Float) {
        loggers.forEach { it.storeFloatVariable(key, value) }
    }

    fun storeIntVariable(key: String, value: Int) {
        loggers.forEach { it.storeIntVariable(key, value) }
    }

    fun storeStringVariable(key: String, value: String) {
        loggers.forEach { it.storeStringVariable(key, value) }
    }

    interface Logger {
        fun storeBoolVariable(key: String, value: Boolean)
        fun storeDoubleVariable(key: String, value: Double)
        fun storeFloatVariable(key: String, value: Float)
        fun storeIntVariable(key: String, value: Int)
        fun storeStringVariable(key: String, value: String)
    }


    object Keys {
        val INT_NUM_APPS = "num_apps"
        val STRING_LOCALE = "locale"
    }
}