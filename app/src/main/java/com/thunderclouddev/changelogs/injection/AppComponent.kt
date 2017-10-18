/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.injection

import com.squareup.picasso.Picasso
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.TestAuthActivity
import com.thunderclouddev.changelogs.service.PackageChangedBroadcastReceiver
import com.thunderclouddev.changelogs.ui.MainActivity
import com.thunderclouddev.changelogs.ui.home.AppInfosByPackageView
import com.thunderclouddev.changelogs.ui.home.HomeController
import com.thunderclouddev.changelogs.ui.onboarding.AuthActivity
import com.thunderclouddev.dataprovider.DataProviderModule
import dagger.Component
import javax.inject.Singleton

/**
 * @author David Whitman on 28 Mar, 2017.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, DataProviderModule::class))
interface AppComponent {
    fun getImageLoader(): Picasso

    fun inject(testAuthActivity: TestAuthActivity)
    fun inject(testAuthActivity: AuthActivity)
    fun inject(baseApp: BaseApp)
    fun inject(packageChangedBroadcastReceiver: PackageChangedBroadcastReceiver)
    fun inject(appInfosByPackageView: AppInfosByPackageView)
    fun inject(mainActivity: MainActivity)
    fun inject(homeController: HomeController)
}