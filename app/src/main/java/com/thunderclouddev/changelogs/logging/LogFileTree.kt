/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.logging

import android.content.Context
import timber.log.Timber
import java.io.File

/**
 * Created by david on 7/3/16.
 */
class LogFileTree constructor(val context: Context) : Timber.Tree() {
    val filename = "log.txt"

    override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
        val file = File(context.filesDir, filename)

        if (message != null) {
            file.appendText(message)
        }
    }
}