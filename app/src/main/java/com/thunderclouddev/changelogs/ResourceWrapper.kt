/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs

import android.content.Context
import android.support.annotation.StringRes

/**
 * @author David Whitman on 29 Mar, 2017.
 */
class ResourceWrapper(private val context: Context) {
    fun getString(@StringRes stringId: Int): String = context.getString(stringId)
    fun getString(@StringRes stringId: Int, vararg args: String): String = context.getString(stringId, args)
}