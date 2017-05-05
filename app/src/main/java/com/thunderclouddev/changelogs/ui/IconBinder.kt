/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.dataprovider.AppInfo
import com.thunderclouddev.persistence.Links

/**
 * Set an icon to an [ImageView], first trying to get the icon locally, then online, and then using a placeholder.
 * Created by david on 4/26/17.
 */
object IconBinder {
    fun bindIcon(appInfo: AppInfo, imageView: ImageView) {
        val iconUri = AppIconRequestHandler.APP_ICON_SCHEME + appInfo.packageName

        BaseApp.appInjector.getImageLoader()
                .load(iconUri)
                .into(imageView, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        BaseApp.appInjector.getImageLoader()
                                .load(appInfo.links?.get(Links.ICON)?.firstOrNull() ?: "error")
                                .error(R.mipmap.ic_launcher_round)
                                .into(imageView)
                    }
                })
    }
}