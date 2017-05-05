/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui

import android.content.Context
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import com.thunderclouddev.changelogs.adaptors.AppInfoBuilder
import java.lang.ref.WeakReference

/**
 * Loads an app icon.
 *
 * @author David Whitman on 2/28/2016.
 */
class AppIconRequestHandler(val context: Context, val sizeX: Int, val sizeY: Int) : RequestHandler() {

    companion object {
        val APP_ICON_SCHEME = "appIcon://"
    }

    /**
     * Whether or not this `RequestHandler` can handle a request with the given `Request`.
     */
    override fun canHandleRequest(data: Request?) = data?.uri?.toString()?.startsWith(APP_ICON_SCHEME) ?: false

    /**
     * Loads an image for the given `Request`.
     *
     * @param request the data from which the image should be resolved.
     * @param networkPolicy the `NetworkPolicy` for this request.
     */
    override fun load(request: Request?, networkPolicy: Int): Result? {
        if (request == null || request.uri == null) {
            return null
        }

        val packageName = request.uri.toString().removePrefix(APP_ICON_SCHEME)
        val icon = WeakReference(AppInfoBuilder.getAppIcon(context, packageName.toString(), sizeX, sizeY))
        return if (icon.get() == null) null else Result(icon.get()?.bitmap, Picasso.LoadedFrom.DISK)
    }
}