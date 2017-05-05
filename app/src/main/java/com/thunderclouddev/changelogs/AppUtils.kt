/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs

import com.thunderclouddev.dataprovider.AppInfo
import com.thunderclouddev.dataprovider.AppInfosByPackage

/**
 * Created by david on 4/30/17.
 */
object AppUtils {
    fun findCurrentlyInstalledApp(appInfosByPackage: AppInfosByPackage, installedPackages: InstalledPackages): AppInfo? =
            appInfosByPackage.asMapByVersionCode[installedPackages.get.find { it.packageName.equals(appInfosByPackage.packageName, ignoreCase = true) }?.versionCode]
}