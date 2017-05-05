/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.thunderclouddev.changelogs.preferences.UserPreferences
import com.thunderclouddev.changelogs.logging.VariableLogger
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


/**
 * The packages installed on the local device
 *
 * @author David Whitman on 12/13/2015.
 */
@Singleton
open class InstalledPackages @Inject constructor(private val packageManager: PackageManager,
                                                 private val preferences: UserPreferences,
                                                 private val variableLogger: VariableLogger) {
    var get: List<PackageInfo> = getInstalledPackages().blockingGet()

    fun numberOfInstalledApps(): Int {
        return get.size
    }

    fun refresh(): Completable =
            getInstalledPackages()
                    .map { get = it }
                    .doOnError { error -> Timber.e(error) }
                    .toCompletable()

    private fun getInstalledPackages(): Single<List<PackageInfo>> =
            Single.create<List<PackageInfo>> { s ->
                val scanStarted = Date()

                var packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA) // TODO: GET_META_DATA isn't a valid flag for packages, only apps

                if (!preferences.showSystemAppsPreference) {
                    packages = packages.filterNot { isSystemPackage(it) }
                }

                Timber.v("Local scan took ${Date().time - scanStarted.time} millis to find ${packages.size} packages")

                val hydratingStarted = Date()

                val pool = Executors.newFixedThreadPool(10)

                for (packageInfo in packages) {
                    pool.submit({
                        try {
                            hydratePackageInfo(packageInfo, packageManager)
                        } catch (e: PackageManager.NameNotFoundException) {
                            Timber.w(e, e.message)
                        }
                    })
                }

                pool.shutdown()
                pool.awaitTermination(60, TimeUnit.SECONDS)

                Timber.v("Hydrating ${packages.size} local apps took ${Date().time - hydratingStarted.time} millis.")
                variableLogger.storeIntVariable(VariableLogger.Keys.INT_NUM_APPS, packages.size)
                s.onSuccess(packages)
            }

    private fun hydratePackageInfo(packageInfo: PackageInfo, pm: PackageManager) {
        packageInfo.applicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
        packageInfo.applicationInfo.name = pm.getApplicationLabel(packageInfo.applicationInfo).toString()
    }

    /**
     * True if package is a system app with no update.
     * False if the package is not a system app, or if it is a system app but has no update.
     */
    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return getFlag(pkgInfo, ApplicationInfo.FLAG_SYSTEM) != 0 || getFlag(pkgInfo, ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    }

    private fun getFlag(pkgInfo: PackageInfo, flag: Int): Int {
        return pkgInfo.applicationInfo.flags and flag
    }
}
