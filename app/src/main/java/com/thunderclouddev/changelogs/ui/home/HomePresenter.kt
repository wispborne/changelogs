/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.dataprovider.Database
import com.thunderclouddev.dataprovider.PlayClient
import com.thunderclouddev.dataprovider.Progress
import com.thunderclouddev.utils.plusAssign
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * @author David Whitman on 08 May, 2017.
 */
class HomePresenter @Inject constructor(private val ui: HomeUi,
                                        private val intentions: HomeUi.Intentions,
                                        private val playClient: PlayClient,
                                        private val installedPackages: InstalledPackages,
                                        private val database: Database) {
    val disposables = CompositeDisposable()

    fun start() {
        val startScan = intentions.scanForUpdatesRequest()
//                .map { playClient.getApps() } // todo this is not correct for the given request
                .map { playClient.scanForUpdates(installedPackages.get.map { it.packageName }) }
                .map { HomeUi.State.Change.UpdateLoadingMarketBulkDetails(Progress(inProgress = true)) }
                .cast(HomeUi.State.Change::class.java)
                .share()
                .onErrorReturn { HomeUi.State.Change.Error(RuntimeException(it)) }

        val loadApps = intentions.loadCachedItems()
//                .map { playClient.getApps() }
                .map { HomeUi.State.Change.ReadFromDatabaseRequested }
                .cast(HomeUi.State.Change::class.java)
                .share()
                .onErrorReturn { HomeUi.State.Change.Error(RuntimeException(it)) }

        val clearDatabase = intentions.clearDatabase()
                .flatMap { database.clear().toObservable<HomeUi.State.Change>() }
                .onErrorReturn(handleUnknownError)

//        val appsLoaded = playClient.databaseChanges
//                .filter { it is PlayClient.DatabaseChange.Success }
//                .cast(PlayClient.DatabaseChange.Success::class.java)
//                .map { HomeUi.State.Change.ReadFromDatabaseComplete(it.appInfos) }
//                .share()

        disposables += startScan
                .mergeWith(loadApps)
                .mergeWith(clearDatabase)
                .doOnNext { Timber.v(it.logString) }
                .scan(ui.state, HomeUi.State::reduce)
                .doOnNext { Timber.v(it.toString()) }
                .subscribe(ui::render)
    }

    fun stop() {
        disposables.clear()
    }

    private val handleUnknownError: (Throwable) -> HomeUi.State.Change = { t -> HomeUi.State.Change.Error(t) }
}