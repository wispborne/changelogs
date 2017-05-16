/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.dataprovider.PlayClient
import timber.log.Timber
import javax.inject.Inject

/**
 * Receives an [Intent] when a package is added, replaced, or removed.
 *
 * @author David Whitman on 08 May, 2017.
 */
class PackageChangedBroadcastReceiver : BroadcastReceiver() {
    private val dataPrefix = "package:"

    @Inject lateinit var playClient: PlayClient
    @Inject lateinit var installedPackages: InstalledPackages

    override fun onReceive(context: Context?, intent: Intent?) {
        BaseApp.appInjector.inject(this)

        if (intent == null) {
            return
        }

        if (intent.action == null) {
            Timber.d("Action was null for package change intent")
            return
        }

        if (intent.data == null) {
            Timber.d("Data was null for package change with action ${intent.action}")
            return
        }

        val packageName = intent.data.toString().removePrefix(dataPrefix)

        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            Timber.v("Package removed: $packageName")
        } else if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            Timber.v("Package added: $packageName")
        } else if (intent.action == Intent.ACTION_PACKAGE_REPLACED) {
            Timber.v("Package replaced: $packageName")
        } else {
            Timber.w("Intent received with unhandled action: ${intent.action}")
            return
        }

        
    }
}