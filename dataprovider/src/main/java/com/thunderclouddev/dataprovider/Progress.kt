/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

/**
 * Represents some action that is in progress.
 *
 * @author David Whitman on 07 May, 2017.
 */
data class Progress(val min: Int = 0,
                    val max: Int = 100,
                    val current: Int = 0,
                    val inProgress: Boolean) {
    companion object {
        val NONE by lazy {
            Progress(inProgress = false)
        }
    }
}