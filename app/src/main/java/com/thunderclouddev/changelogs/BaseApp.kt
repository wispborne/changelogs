/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs

import android.app.Application
import android.support.annotation.CallSuper
import com.thunderclouddev.changelogs.injection.AppComponent
import com.thunderclouddev.changelogs.injection.AppModule
import com.thunderclouddev.changelogs.injection.DaggerAppComponent
import com.thunderclouddev.changelogs.logging.LogFileTree
import com.thunderclouddev.changelogs.logging.VariableLogger
import com.thunderclouddev.changelogs.preferences.Preferences
import com.thunderclouddev.changelogs.preferences.UserPreferences
import com.thunderclouddev.dataprovider.DataProviderModule
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KProperty

/**
 * @author David Whitman on 28 Mar, 2017.
 */
open class BaseApp : Application() {
    companion object {
        lateinit var appInjector: AppComponent
    }

    @Inject lateinit var variableLogger: VariableLogger
    @Inject lateinit var buildConfig: com.thunderclouddev.changelogs.preferences.BuildConfig
    @Inject lateinit var preferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        appInjector = DaggerAppComponent.builder()
                .dataProviderModule(DataProviderModule())
                .appModule(AppModule(this))
                .build()
        BaseApp.appInjector.inject(this)

        Preferences.init(this)
        initLogging()
        DataProviderModule.debugMode = BuildConfig.DEBUG

    }

    @CallSuper
    open fun initLogging() {
        Timber.uprootAll()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (buildConfig.enableTrace) {
            Timber.plant(LogFileTree(this))
        }

        preferences.addListener(object : Preferences.SharedPrefsListener {
            override fun onSharedPrefChanged(property: KProperty<*>) {
                if (UserPreferences::allowAnonymousLogging.name == property.name) {
                    initLogging()
                }
            }
        })
    }
}