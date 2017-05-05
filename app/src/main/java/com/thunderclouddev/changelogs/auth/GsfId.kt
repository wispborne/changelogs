/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.auth

import android.content.Context
import android.net.Uri
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gets the device's ID for Google Services Framework.
 * REQUIRES com.google.android.providers.gsf.permission.READ_GSERVICES
 * Credit to http://stackoverflow.com/questions/22743087/gsf-id-key-google-service-framework-id-as-android-device-unique-identifier
 */
@Singleton
class GsfId @Inject constructor(private val context: Context) {
    val get: String? by lazy {
        val uri = Uri.parse("content://com.google.android.gsf.gservices")
        val params = arrayOf("android_id")
        val c = context.contentResolver.query(uri, null, null, params, null)

        if (c != null) {
            if (c.moveToFirst() && c.columnCount >= 2) {
                try {
                    val androidId = java.lang.Long.toHexString(c.getString(1).toLong())
                    Timber.v("AndroidId=$androidId")
                    androidId
                } catch (e: Exception) {
                    Timber.e(e, "Failed to get androidId!")
                    null
                } finally {
                    c.close()
                }
            } else {
                Timber.e(RuntimeException(), "Failed to get androidId, cursor was null!")
                null
            }
        } else {
            Timber.w(RuntimeException(), "Failed to get androidId!")
            null
        }
    }
}