/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import android.support.v7.util.DiffUtil
import com.thunderclouddev.changelogs.ui.StateRenderer
import com.thunderclouddev.dataprovider.AppInfosByPackage
import com.thunderclouddev.dataprovider.Progress
import io.reactivex.Observable

/**
 * @author David Whitman on 07 May, 2017.
 */
interface HomeUi : StateRenderer<HomeUi.State> {
    val state: State

    override fun render(state: State)

    interface Intentions {
        fun scanForUpdatesRequest(): Observable<Unit>
        fun loadCachedItems(): Observable<Unit>
        fun clearDatabase(): Observable<Unit>
        fun refresh(): Observable<Unit>
        fun addTestApp(): Observable<Unit>
        fun addTestApps(): Observable<Unit>
        fun removeTestApp(): Observable<Unit>
    }

    interface Actions {
        fun displayItems(diffResult: RecyclerViewBinding<AppInfosByPackage>)

        fun showLoading(marketBulkDetailsProgress: Progress)

        fun setRefreshEnabled(enabled: Boolean)

        fun showError(error: String)

        fun hideError()
    }

    data class State(val appInfos: List<AppInfosByPackage>,
            //                     val detectingLocalAppsProgress: Progress,
//                     val fdfeBulkDetailsProgress: Progress,
                     val marketBulkDetailsProgress: Progress
            //                     val fdfeDetailsProgress: Progress
    ) {
        fun reduce(change: Change): State = when (change) {
            is Change.UpdateLoadingMarketBulkDetails -> this.copy(marketBulkDetailsProgress = change.refreshingState)
            is Change.Error -> this.copy()
            is Change.ReadFromDatabaseComplete -> this.copy(appInfos = change.data)
            Change.ReadFromDatabaseRequested -> this.copy()
        }

        fun isLoading() = marketBulkDetailsProgress.inProgress

        sealed class Change(val logString: String) {
            //            class UpdateDetectingLocalChanges(val refreshingState: Progress)
//            class UpdateLoadingFdfeBulkDetails(val refreshingState: Progress)
            class UpdateLoadingMarketBulkDetails(val refreshingState: Progress) : Change("Updated market bulk details - $refreshingState")

            class ReadFromDatabaseComplete(val data: List<AppInfosByPackage>) : Change("Read apps from database - $data")

            //            class UpdateLoadingFdfeDetails(val refreshingState: Progress)
            class Error(val throwable: Throwable) : Change("Error - ${throwable.message}")

            object ReadFromDatabaseRequested : Change("Read from database requested")
        }

        companion object {
            val EMPTY by lazy {
                State(appInfos = emptyList(),
                        marketBulkDetailsProgress = Progress.NONE)
            }
        }
    }
}

data class RecyclerViewBinding<out T>(
        val new: List<T>,
        val diff: DiffUtil.DiffResult
)