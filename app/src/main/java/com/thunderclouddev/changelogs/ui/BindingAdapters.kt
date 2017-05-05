/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui

import android.annotation.SuppressLint
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.os.Build
import android.text.Html
import android.text.format.DateFormat
import android.widget.ImageView
import android.widget.TextView
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.BuildConfig
import com.thunderclouddev.utils.empty
import java.util.*

/**
 * Sets the given [Bitmap] as the [Drawable] of the input [ImageView]
 */
@BindingAdapter("android:src")
fun setImageBitmap(imageView: ImageView, bitmap: Bitmap?) {
    imageView.setImageBitmap(bitmap)
}


/**
 * Sets the given [String] as the [TextView]s text, using html formatting
 */
@SuppressLint("NewApi")
@BindingAdapter("android:htmlText")
fun setHtmlText(textView: TextView, string: String) {
    textView.text =
            if (BuildConfig.VERSION_CODE >= Build.VERSION_CODES.N)
                Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
            else
                Html.fromHtml(string)
}

/**
 * Accepts a uri of the format `appIcon://com.package.name`
 */
@BindingAdapter("bind:appIcon")
fun setAppIconDrawable(imageView: ImageView, packageNameUri: String) {
    BaseApp.appInjector.getImageLoader()
            .load(packageNameUri)
            .into(imageView)
}

@BindingAdapter("bind:shortDateText")
fun setShortDateText(textView: TextView, date: Date?) {
    textView.text = if (date == null)
        String.empty
    else
        DateFormat.getMediumDateFormat(textView.context).format(date)
}