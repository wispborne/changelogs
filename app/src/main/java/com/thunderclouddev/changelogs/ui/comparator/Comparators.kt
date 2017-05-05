/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.comparator

import com.thunderclouddev.changelogs.ui.home.AppInfoRecycler
import java.util.*

/**
 * Created by David Whitman on 4/22/2015.

 * @author David Whitman
 */
object Comparators {

    val Default = Sorting.ByRecentlyUpdated

//    private val alphabeticalByTitleComparator = AlphabeticalByTitleComparator()
    private val recentlyUpdatedComparator = RecentlyUpdatedComparator()
//    private val hasUpdateComparator = HasUpdateComparator()
//    private val sizeComparator = SizeComparator()

    operator fun get(comparatorType: Sorting = Default): Comparator<AppInfoRecycler.AppInfoViewModel> {
        when (comparatorType) {
//            Sorting.ByTitle -> return alphabeticalByTitleComparator
//            Sorting.ByHasUpdate -> return hasUpdateComparator
            Sorting.ByRecentlyUpdated -> return recentlyUpdatedComparator
//            Sorting.BySize -> return sizeComparator
            else -> return recentlyUpdatedComparator
        }
    }
}
