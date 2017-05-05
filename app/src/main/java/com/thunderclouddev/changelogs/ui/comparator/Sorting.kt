/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.comparator

/**
 * @author David Whitman
 */
enum class Sorting(val value: Int) {
    ByRecentlyUpdated(1),
    ByTitle(2),
    ByHasUpdate(3),
    BySize(4),
    Default(1);

    companion object {
        fun valueOf(value: Int): Sorting {
            return when(value) {
                1 -> ByRecentlyUpdated
                2 -> ByTitle
                3 -> ByHasUpdate
                4 -> BySize
                else -> Default
            }
        }
    }
}