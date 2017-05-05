/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import android.content.Context
import com.thunderclouddev.persistence.AppInfoDatabase

/**
 * Created by david on 4/25/17.
 */
object Injector {
    fun buildAppInfoDatabase(context: Context, debugMode: Boolean) = AppInfoDatabase.create(context, debugMode)
}