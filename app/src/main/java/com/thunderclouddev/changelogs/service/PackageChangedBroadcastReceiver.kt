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
        Timber.v("Broadcast received: ${intent?.extras.toString()}")

        if (intent != null && intent.action != null) {
            if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
                Timber.v("Package removed: ${intent.data.toString().removePrefix(dataPrefix)}")
            } else if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
                Timber.v("Package added: ${intent.data.toString().removePrefix(dataPrefix)}")
            } else if (intent.action == Intent.ACTION_PACKAGE_REPLACED) {
                Timber.v("Package replaced: ${intent.data.toString().removePrefix(dataPrefix)}")
            }
        }
    }
}