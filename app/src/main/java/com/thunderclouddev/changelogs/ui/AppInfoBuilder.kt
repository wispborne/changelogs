/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.adaptors

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

/**
 * @author Thundercloud Dev on 3/5/2015.
 */
object AppInfoBuilder {
    fun getAppIcon(context: Context, packageName: String, sizeX: Int, sizeY: Int): BitmapDrawable? {
        try {
            Timber.v("Getting icon for app $packageName")
            val drawable = WeakReference(context.packageManager.getApplicationIcon(packageName))

            return if (sizeX == 0 && sizeY == 0) {
                Timber.w("Loading full-resolution icon for $packageName!")
                drawable.get() as BitmapDrawable
            } else if (drawable.get() != null) {
                BitmapDrawable(context.resources, Bitmap.createScaledBitmap((drawable.get() as BitmapDrawable).bitmap, sizeX, sizeY, true))
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.w(e.message, e, packageName)
            return null
        }
    }

    fun getAppSize(packageManager: PackageManager, packageName: String): Long? {
        try {
            return File(packageManager.getApplicationInfo(packageName, 0).publicSourceDir).length()
        } catch(e: Exception) {
            Timber.w(e.message, e, packageName)
            return null
        }
    }
}
