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
 * Created by David Whitman on 4/6/2015.

 * @author David Whitman
 */
internal class RecentlyUpdatedComparator : Comparator<AppInfoRecycler.AppInfoViewModel> {
    override fun compare(left: AppInfoRecycler.AppInfoViewModel, right: AppInfoRecycler.AppInfoViewModel) =
            (right.mostRecentAppInfo.updateDate?.time ?: 0).compareTo(left.mostRecentAppInfo.updateDate?.time ?: 0)
}
