/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.comparator

import java.util.*

/**
 * @author Thundercloud Dev on 6/9/2016.
 */
//class SizeComparator : Comparator<AppInfo> {
//    companion object {
//        // Performance, yo
//        private val recentlyUpdatedComparator = RecentlyUpdatedComparator()
//    }
//
//    override fun compare(lhs: AppInfo?, rhs: AppInfo?): Int {
//        val leftMC = MostCorrectAppInfo(lhs!!)
//        val rightMC = MostCorrectAppInfo(rhs!!)
//        val updateResult = rightMC.installSize.compareTo(leftMC.installSize)
//
//        if (updateResult != 0) {
//            return updateResult
//        } else {
//            return recentlyUpdatedComparator.compare(lhs, rhs)
//        }
//    }
//}