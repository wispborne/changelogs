/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.injection

import android.app.Application
import android.content.Context
import android.os.Build
import com.squareup.picasso.Picasso
import com.thunderclouddev.changelogs.NativeDeviceInfo
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.auth.GsfId
import com.thunderclouddev.changelogs.preferences.BuildConfig
import com.thunderclouddev.changelogs.ui.AppIconRequestHandler
import com.thunderclouddev.changelogs.ui.onboarding.AuthActivity
import com.thunderclouddev.playstoreapi.AuthorizationRequiredDelegate
import com.thunderclouddev.playstoreapi.DeviceInfo
import com.thunderclouddev.playstoreapi.PlayApiClientWrapper
import com.thunderclouddev.playstoreapi.legacyMarketApi.LegacyApiClientWrapper
import com.thunderclouddev.utils.empty
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

/**
 * @author David Whitman on 28 Mar, 2017.
 */
@Module
class AppModule(private val application: Application) {

    /**
     * Allow the application context to be injected but require that it be annotated with [ForApplication] to explicitly differentiate it from an activity context.
     */
    @Singleton
    @Provides
    fun provideAppContext(): Context = application

    // TODO Move this logic somewhere else
    @Singleton
    @Provides
    fun providePlayApiClientWrapper(context: Context, gsfId: GsfId) = PlayApiClientWrapper(context,
            gsfId.get ?: String.empty,
            DeviceInfo(
                    NativeDeviceInfo(context, Locale.US.toString()).generateAndroidCheckinRequest(),
                    DeviceInfo.generateUserAgentString(Build.VERSION.SDK_INT, Build.DEVICE, Build.HARDWARE, Build.PRODUCT),
                    Build.VERSION.SDK_INT
            ),
            object : AuthorizationRequiredDelegate {
                override fun onAuthorizationRequired() {
                    AuthActivity.promptUserForFdfeToken(context)
                }
            })

    // TODO Move this logic somewhere else
    @Singleton
    @Provides
    fun provideLegacyApiClientWrapper(context: Context, gsfId: GsfId) = LegacyApiClientWrapper(context,
            gsfId.get ?: String.empty,
            object : AuthorizationRequiredDelegate {
                override fun onAuthorizationRequired() {
                    AuthActivity.promptUserForLegacyToken(context)
                }
            })

    @Singleton
    @Provides
    fun provideImageLoader(context: Context): Picasso {
        val scalingFactor = 2
        val size = context.resources.getDimensionPixelSize(R.dimen.appInfo_icon_size) * scalingFactor
        return Picasso.Builder(context)
                .addRequestHandler(AppIconRequestHandler(context, size, size))
                .build()
    }

    @Singleton
    @Provides
    fun providePackageManager(context: Context) = context.packageManager

    @Singleton
    @Provides
    fun provideBuildConfig() = BuildConfig(com.thunderclouddev.changelogs.BuildConfig.enableCrashlytics,
            com.thunderclouddev.changelogs.BuildConfig.enableTrace)
}