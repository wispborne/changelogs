/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import java.util.*

/**
 * All versions of the same app.
 * Created by david on 4/30/17.
 */
data class AppInfosByPackage(val packageName: String, val appInfos: List<AppInfo>) {
    val asMap: Map<String, List<AppInfo>> by lazy { HashMap<String, List<AppInfo>>().apply { put(packageName, appInfos) } }

    val mostRecentVersion = appInfos.sortedByDescending { it.versionCode }.first()

    val asMapByVersionCode: Map<Int, AppInfo> by lazy { appInfos.associateBy { it.versionCode } }

    override fun hashCode(): Int {
        return Objects.hash(packageName, Arrays.hashCode(appInfos.toTypedArray()))
    }

    override fun toString(): String {
        return "$packageName versions: {${appInfos.joinToString { it.versionCode.toString() }}}"
    }
}